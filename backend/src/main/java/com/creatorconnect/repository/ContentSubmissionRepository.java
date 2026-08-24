package com.creatorconnect.repository;

import com.creatorconnect.entity.ContentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentSubmissionRepository extends JpaRepository<ContentSubmission, Long> {
    Optional<ContentSubmission> findByApplicationId(Long applicationId);
}
