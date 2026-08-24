package com.creatorconnect.service;

import com.creatorconnect.dto.response.NotificationResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.User;
import com.creatorconnect.entity.enums.NotificationType;

public interface NotificationService {
    void send(User user, NotificationType type, String title, String message, String linkUrl);
    PageResponse<NotificationResponse> getForUser(Long userId, int page, int size);
    long unreadCount(Long userId);
    void markRead(Long userId, Long notificationId);
    void markAllRead(Long userId);
}
