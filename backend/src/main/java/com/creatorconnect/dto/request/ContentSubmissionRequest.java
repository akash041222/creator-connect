package com.creatorconnect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentSubmissionRequest {
    @NotNull
    private Long applicationId;

    private String instagramReelLink;
    private String youtubeLink;
    private String tiktokLink;
    private String driveLink;
    private String comments;
}
