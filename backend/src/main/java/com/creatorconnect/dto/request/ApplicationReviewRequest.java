package com.creatorconnect.dto.request;

import com.creatorconnect.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationReviewRequest {
    @NotNull
    private ApplicationStatus status; // SHORTLISTED, ACCEPTED, REJECTED

    private String rejectionReason;
}
