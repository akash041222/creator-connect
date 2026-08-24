package com.creatorconnect.dto.request;

import com.creatorconnect.entity.enums.SubmissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionReviewRequest {
    @NotNull
    private SubmissionStatus status; // APPROVED, REJECTED, CHANGES_REQUESTED

    private String reviewComments;
}
