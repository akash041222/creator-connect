package com.creatorconnect.dto.request;

import lombok.Data;

@Data
public class CreatorProfileRequest {
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
}
