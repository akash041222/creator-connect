package com.creatorconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatorProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String profilePictureUrl;
    private String coverPhotoUrl;
    private String bio;
    private String location;
    private String languages;
    private String category;
    private Integer experienceYears;
    private Long followerCount;
    private Double engagementRate;
    private String instagramHandle;
    private String youtubeHandle;
    private String linkedinHandle;
    private String facebookHandle;
    private String tiktokHandle;
    private String portfolioUrl;
    private String skills;
    private String achievements;
    private boolean verified;
    private Double averageRating;
    private Integer completedCampaignsCount;
    private Double successRate;
    private BigDecimal totalEarnings;
    private Integer profileCompletionPercent;
}
