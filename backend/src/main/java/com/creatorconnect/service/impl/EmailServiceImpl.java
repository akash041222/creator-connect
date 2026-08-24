package com.creatorconnect.service.impl;

import com.creatorconnect.entity.User;
import com.creatorconnect.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Thin wrapper around Spring Mail. Every send is @Async and swallows/logs
 * failures so a flaky SMTP provider never breaks a core business transaction
 * (e.g. registration should succeed even if the welcome email fails to send).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@creatorconnect.app}")
    private String fromAddress;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    @Async
    public void sendWelcomeEmail(User user) {
        send(user.getEmail(), "Welcome to CreatorConnect 🎉",
                "Hi " + user.getFullName() + ",\n\n" +
                "Welcome to CreatorConnect — where brands meet creators. Your " + user.getRole() +
                " account is ready to go.\n\n" +
                "Get started: " + frontendBaseUrl + "/login.html\n\nTeam CreatorConnect");
    }

    @Override
    @Async
    public void sendPasswordResetEmail(User user) {
        send(user.getEmail(), "Reset your CreatorConnect password",
                "Hi " + user.getFullName() + ",\n\n" +
                "We received a request to reset your password. This link expires in 1 hour:\n" +
                frontendBaseUrl + "/reset-password.html?token=" + user.getResetPasswordToken() +
                "\n\nIf you didn't request this, you can safely ignore this email.");
    }

    @Override
    @Async
    public void sendApplicationStatusEmail(User user, String campaignTitle, String status) {
        send(user.getEmail(), "Application update: " + campaignTitle,
                "Hi " + user.getFullName() + ",\n\n" +
                "Your application to \"" + campaignTitle + "\" is now: " + status + ".\n\n" +
                "View details: " + frontendBaseUrl + "/creator-dashboard.html");
    }

    @Override
    @Async
    public void sendPaymentReleasedEmail(User user, String amount, String invoiceNumber) {
        send(user.getEmail(), "Payment released — " + invoiceNumber,
                "Hi " + user.getFullName() + ",\n\n" +
                "A payment of " + amount + " has been released to you (Invoice " + invoiceNumber + ").\n\n" +
                "View your earnings: " + frontendBaseUrl + "/creator-dashboard.html");
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            // Non-fatal: log and move on. Email delivery should never block core flows.
            log.warn("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }
}
