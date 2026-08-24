package com.creatorconnect.entity;

import com.creatorconnect.entity.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * Completed work uploaded by a creator against an accepted {@link Application}.
 */
@Entity
@Table(name = "content_submissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSubmission extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    private String instagramReelLink;
    private String youtubeLink;
    private String tiktokLink;
    private String driveLink;

    @Column(length = 2000)
    private String comments;

    @Column(name = "submission_date", nullable = false)
    @Builder.Default
    private java.time.LocalDateTime submissionDate = java.time.LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @Column(name = "review_comments", length = 2000)
    private String reviewComments;

    @Column(name = "reviewed_at")
    private java.time.LocalDateTime reviewedAt;
}
