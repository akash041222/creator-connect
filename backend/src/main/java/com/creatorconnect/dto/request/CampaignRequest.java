package com.creatorconnect.dto.request;

import com.creatorconnect.entity.enums.Platform;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CampaignRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull @DecimalMin("0.0")
    private BigDecimal budget;

    private Long minFollowers;

    private Platform preferredPlatform;

    private String category;

    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    private String bannerUrl;
    private String guidelines;
    private String referenceFilesUrl;
    private String deliverables;

    @Min(1)
    private Integer creatorsRequired;
}
