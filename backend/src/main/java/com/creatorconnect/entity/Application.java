package com.creatorconnect.entity;

import com.creatorconnect.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * A creator's application to a campaign. Unique per (campaign, creator).
 */
@Entity
@Table(name = "applications", uniqueConstraints = {
        @UniqueConstraint(name = "uq_campaign_creator", columnNames = {"campaign_id", "creator_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;

    @Column(length = 2000)
    private String message;

    @Column(name = "portfolio_link")
    private String portfolioLink;

    @Column(name = "expected_timeline")
    private String expectedTimeline;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "reviewed_at")
    private java.time.LocalDateTime reviewedAt;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;
}
