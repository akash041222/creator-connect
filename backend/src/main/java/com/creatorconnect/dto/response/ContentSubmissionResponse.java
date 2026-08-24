package com.creatorconnect.dto.response;

import com.creatorconnect.entity.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSubmissionResponse {
    private Long id;
    private Long applicationId;
    private String instagramReelLink;
    private String youtubeLink;
    private String tiktokLink;
    private String driveLink;
    private String comments;
    private LocalDateTime submissionDate;
    private SubmissionStatus status;
    private String reviewComments;
}
