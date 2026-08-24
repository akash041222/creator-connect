package com.creatorconnect.repository;

import com.creatorconnect.entity.SavedCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedCampaignRepository extends JpaRepository<SavedCampaign, Long> {
    Page<SavedCampaign> findByCreatorId(Long creatorId, Pageable pageable);
    Optional<SavedCampaign> findByCampaignIdAndCreatorId(Long campaignId, Long creatorId);
    void deleteByCampaignIdAndCreatorId(Long campaignId, Long creatorId);
}
