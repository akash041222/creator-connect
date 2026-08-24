package com.creatorconnect.service;

import com.creatorconnect.dto.request.*;
import com.creatorconnect.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void verifyEmail(String token);
    AuthResponse refreshToken(String refreshToken);
}
