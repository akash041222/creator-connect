package com.creatorconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended profile for users with role CREATOR. One-to-one with {@link User}.
 */
@Entity
@Table(name = "creators")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Creator extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "cover_photo_url")
    private String coverPhotoUrl;

    @Column(length = 2000)
    private String bio;

    private String location;

    @Column(length = 500)
    private String languages; // comma separated

    private String category; // e.g. Fashion, Tech, Fitness

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "follower_count")
    @Builder.Default
    private Long followerCount = 0L;

    @Column(name = "engagement_rate")
    @Builder.Default
    private Double engagementRate = 0.0;

    private String instagramHandle;
    private String youtubeHandle;
    private String linkedinHandle;
    private String facebookHandle;
    private String tiktokHandle;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(length = 1000)
    private String skills; // comma separated

    @Column(length = 1000)
    private String achievements;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Builder.Default
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Builder.Default
    @Column(name = "completed_campaigns_count")
    private Integer completedCampaignsCount = 0;

    @Builder.Default
    @Column(name = "success_rate")
    private Double successRate = 0.0;

    @Builder.Default
    @Column(name = "total_earnings")
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "profile_completion_percent")
    private Integer profileCompletionPercent = 20;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Application> applications = new ArrayList<>();
}
