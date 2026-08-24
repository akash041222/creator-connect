package com.creatorconnect.service.impl;

import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.dto.response.UserResponse;
import com.creatorconnect.entity.Campaign;
import com.creatorconnect.entity.Company;
import com.creatorconnect.entity.Creator;
import com.creatorconnect.entity.User;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Role;
import com.creatorconnect.entity.enums.UserStatus;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.repository.CampaignRepository;
import com.creatorconnect.repository.CompanyRepository;
import com.creatorconnect.repository.CreatorRepository;
import com.creatorconnect.repository.UserRepository;
import com.creatorconnect.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CreatorRepository creatorRepository;
    private final CampaignRepository campaignRepository;

    @Override
    public PageResponse<UserResponse> listUsers(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> results = (role == null) ? userRepository.findAll(pageable) : userRepository.findByRole(role, pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    @Transactional
    public void suspendUser(Long userId) {
        User user = userOrThrow(userId);
        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void reactivateUser(Long userId) {
        User user = userOrThrow(userId);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userOrThrow(userId);
        user.setStatus(UserStatus.DEACTIVATED);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void verifyCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        company.setVerified(true);
        companyRepository.save(company);
    }

    @Override
    @Transactional
    public void verifyCreator(Long creatorId) {
        Creator creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found: " + creatorId));
        creator.setVerified(true);
        creatorRepository.save(creator);
    }

    @Override
    @Transactional
    public void deleteCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));
        campaign.setDeleted(true);
        campaign.setStatus(CampaignStatus.CANCELLED);
        campaignRepository.save(campaign);
    }

    private User userOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .role(u.getRole())
                .status(u.getStatus())
                .emailVerified(u.isEmailVerified())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
