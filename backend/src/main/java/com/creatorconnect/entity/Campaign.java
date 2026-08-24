package com.creatorconnect.entity;

import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Platform;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A marketing campaign published by a {@link Company} for creators to apply to.
 */
@Entity
@Table(name = "campaigns", indexes = {
        @Index(name = "idx_campaigns_status", columnList = "status"),
        @Index(name = "idx_campaigns_category", columnList = "category")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000, nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(name = "min_followers")
    private Long minFollowers;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_platform")
    private Platform preferredPlatform;

    private String category;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(length = 4000)
    private String guidelines;

    @Column(name = "reference_files_url", length = 2000)
    private String referenceFilesUrl;

    @Column(length = 2000)
    private String deliverables;

    @Column(name = "creators_required")
    @Builder.Default
    private Integer creatorsRequired = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.OPEN;

    @Builder.Default
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Application> applications = new ArrayList<>();
}
