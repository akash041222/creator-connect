package com.creatorconnect.entity;

import jakarta.persistence.*;
import lombok.*;

/** Bookmark: a creator saving a campaign for later. */
@Entity
@Table(name = "saved_campaigns", uniqueConstraints = {
        @UniqueConstraint(name = "uq_saved_campaign_creator", columnNames = {"campaign_id", "creator_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedCampaign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;
}
