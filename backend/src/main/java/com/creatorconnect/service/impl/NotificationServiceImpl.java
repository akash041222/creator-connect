package com.creatorconnect.service.impl;

import com.creatorconnect.dto.response.NotificationResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.Notification;
import com.creatorconnect.entity.User;
import com.creatorconnect.entity.enums.NotificationType;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.exception.UnauthorizedActionException;
import com.creatorconnect.repository.NotificationRepository;
import com.creatorconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void send(User user, NotificationType type, String title, String message, String linkUrl) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .linkUrl(linkUrl)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public PageResponse<NotificationResponse> getForUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> results = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("This notification does not belong to you.");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        Pageable pageable = PageRequest.of(0, 200);
        Page<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread.getContent());
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .linkUrl(n.getLinkUrl())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
