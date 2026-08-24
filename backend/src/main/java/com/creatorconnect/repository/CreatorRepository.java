package com.creatorconnect.repository;

import com.creatorconnect.entity.Creator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByUserId(Long userId);

    @Query("SELECT c FROM Creator c WHERE " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:location IS NULL OR c.location = :location) AND " +
           "(:minFollowers IS NULL OR c.followerCount >= :minFollowers) AND " +
           "c.deleted = false")
    Page<Creator> search(@Param("category") String category,
                          @Param("location") String location,
                          @Param("minFollowers") Long minFollowers,
                          Pageable pageable);

    Page<Creator> findAllByOrderByAverageRatingDesc(Pageable pageable);
    Page<Creator> findAllByOrderByFollowerCountDesc(Pageable pageable);
}
