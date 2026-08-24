package com.creatorconnect.entity;

import jakarta.persistence.*;
import lombok.*;

/** Human-facing "recent activity" feed entries (dashboard timelines). */
@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String action; // e.g. "APPLIED_TO_CAMPAIGN"

    @Column(length = 1000)
    private String description;

    @Column(name = "entity_type")
    private String entityType; // e.g. "Campaign"

    @Column(name = "entity_id")
    private Long entityId;
}
