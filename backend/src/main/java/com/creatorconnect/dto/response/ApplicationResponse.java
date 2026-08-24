package com.creatorconnect.dto.response;

import com.creatorconnect.entity.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long campaignId;
    private String campaignTitle;
    private Long creatorId;
    private String creatorName;
    private String creatorProfilePicUrl;
    private Long creatorFollowers;
    private String message;
    private String portfolioLink;
    private String expectedTimeline;
    private ApplicationStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
