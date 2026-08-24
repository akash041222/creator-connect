package com.creatorconnect.service;

import com.creatorconnect.entity.User;

public interface EmailService {
    void sendWelcomeEmail(User user);
    void sendPasswordResetEmail(User user);
    void sendApplicationStatusEmail(User user, String campaignTitle, String status);
    void sendPaymentReleasedEmail(User user, String amount, String invoiceNumber);
}
