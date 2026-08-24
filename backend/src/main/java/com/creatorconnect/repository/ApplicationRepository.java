package com.creatorconnect.repository;

import com.creatorconnect.entity.Application;
import com.creatorconnect.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findByCreatorId(Long creatorId, Pageable pageable);
    Page<Application> findByCampaignId(Long campaignId, Pageable pageable);
    Page<Application> findByCampaignCompanyId(Long companyId, Pageable pageable);
    Optional<Application> findByCampaignIdAndCreatorId(Long campaignId, Long creatorId);
    long countByCreatorIdAndStatus(Long creatorId, ApplicationStatus status);
    long countByCampaignCompanyId(Long companyId);
    long countByStatus(ApplicationStatus status);
}
