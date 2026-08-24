package com.creatorconnect.controller;

import com.creatorconnect.dto.response.ApiResponse;
import com.creatorconnect.dto.response.NotificationResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.service.NotificationService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification feed")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(notificationService.getForUser(SecurityUtil.currentUserId(), page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount() {
        return ResponseEntity.ok(notificationService.unreadCount(SecurityUtil.currentUserId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markRead(@PathVariable Long id) {
        notificationService.markRead(SecurityUtil.currentUserId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read."));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse> markAllRead() {
        notificationService.markAllRead(SecurityUtil.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read."));
    }
}
