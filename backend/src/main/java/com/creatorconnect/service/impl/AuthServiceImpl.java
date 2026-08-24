package com.creatorconnect.service.impl;

import com.creatorconnect.dto.request.*;
import com.creatorconnect.dto.response.AuthResponse;
import com.creatorconnect.entity.Company;
import com.creatorconnect.entity.Creator;
import com.creatorconnect.entity.User;
import com.creatorconnect.entity.enums.Role;
import com.creatorconnect.entity.enums.UserStatus;
import com.creatorconnect.exception.DuplicateResourceException;
import com.creatorconnect.exception.InvalidRequestException;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.repository.CompanyRepository;
import com.creatorconnect.repository.CreatorRepository;
import com.creatorconnect.repository.UserRepository;
import com.creatorconnect.security.CustomUserDetails;
import com.creatorconnect.security.JwtUtil;
import com.creatorconnect.service.AuthService;
import com.creatorconnect.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CreatorRepository creatorRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }
        if (request.getRole() == Role.ADMIN) {
            throw new InvalidRequestException("Admin accounts cannot self-register.");
        }
        if (request.getRole() == Role.COMPANY && isBlank(request.getCompanyName())) {
            throw new InvalidRequestException("Company name is required for company accounts.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .status(UserStatus.ACTIVE) // simplified: auto-active; flip to PENDING_VERIFICATION if enforcing email verification
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .build();
        user = userRepository.save(user);

        if (request.getRole() == Role.CREATOR) {
            Creator creator = Creator.builder().user(user).build();
            creatorRepository.save(creator);
        } else if (request.getRole() == Role.COMPANY) {
            Company company = Company.builder().user(user).companyName(request.getCompanyName()).build();
            companyRepository.save(company);
        }

        emailService.sendWelcomeEmail(user);

        return buildAuthResponse(user, false);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new InvalidRequestException("This account has been suspended. Contact support.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setRememberMe(request.isRememberMe());
        userRepository.save(user);

        return buildAuthResponse(user, request.isRememberMe());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found for this email."));
        user.setResetPasswordToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new InvalidRequestException("Invalid or expired reset token."));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Reset token has expired. Please request a new one.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidRequestException("Invalid verification token."));
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return buildAuthResponse(user, user.isRememberMe());
    }

    private AuthResponse buildAuthResponse(User user, boolean rememberMe) {
        CustomUserDetails principal = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateToken(principal, user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(principal);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
