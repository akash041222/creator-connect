package com.creatorconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {
    private Long id;
    private String companyName;
    private String email;
    private String logoUrl;
    private String website;
    private String industry;
    private String description;
    private String location;
    private boolean verified;
    private Integer totalCampaigns;
    private BigDecimal totalSpending;
    private Double averageRating;
}
