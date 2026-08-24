package com.creatorconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {
    @NotNull
    private Long campaignId;

    @NotBlank
    private String message;

    private String portfolioLink;
    private String expectedTimeline;
}
