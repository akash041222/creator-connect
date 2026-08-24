package com.creatorconnect.dto.response;

import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private boolean companyVerified;
    private String title;
    private String description;
    private BigDecimal budget;
    private Long minFollowers;
    private Platform preferredPlatform;
    private String category;
    private LocalDate deadline;
    private String bannerUrl;
    private String guidelines;
    private String deliverables;
    private Integer creatorsRequired;
    private CampaignStatus status;
    private Long viewCount;
    private long applicationCount;
    private LocalDateTime createdAt;
}
