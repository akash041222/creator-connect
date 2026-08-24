package com.creatorconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended profile for users with role COMPANY. One-to-one with {@link User}.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "logo_url")
    private String logoUrl;

    private String website;

    private String industry;

    @Column(length = 2000)
    private String description;

    private String location;

    private String instagramHandle;
    private String linkedinHandle;
    private String twitterHandle;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Builder.Default
    @Column(name = "total_campaigns")
    private Integer totalCampaigns = 0;

    @Builder.Default
    @Column(name = "total_spending")
    private BigDecimal totalSpending = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Campaign> campaigns = new ArrayList<>();
}
