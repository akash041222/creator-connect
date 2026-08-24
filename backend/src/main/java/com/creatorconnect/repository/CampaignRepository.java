package com.creatorconnect.repository;

import com.creatorconnect.entity.Campaign;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Page<Campaign> findByCompanyId(Long companyId, Pageable pageable);
    Page<Campaign> findByStatus(CampaignStatus status, Pageable pageable);
    long countByStatus(CampaignStatus status);
    long countByCompanyId(Long companyId);

    @Query("SELECT c FROM Campaign c WHERE c.deleted = false AND " +
           "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:platform IS NULL OR c.preferredPlatform = :platform) AND " +
           "(:minBudget IS NULL OR c.budget >= :minBudget) AND " +
           "(:maxBudget IS NULL OR c.budget <= :maxBudget) AND " +
           "(:minFollowers IS NULL OR c.minFollowers <= :minFollowers) AND " +
           "(:deadlineBefore IS NULL OR c.deadline <= :deadlineBefore) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Campaign> search(@Param("keyword") String keyword,
                           @Param("category") String category,
                           @Param("platform") Platform platform,
                           @Param("minBudget") BigDecimal minBudget,
                           @Param("maxBudget") BigDecimal maxBudget,
                           @Param("minFollowers") Long minFollowers,
                           @Param("deadlineBefore") LocalDate deadlineBefore,
                           @Param("status") CampaignStatus status,
                           Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.status = 'OPEN' AND c.deleted = false ORDER BY c.viewCount DESC")
    Page<Campaign> findTrending(Pageable pageable);
}
