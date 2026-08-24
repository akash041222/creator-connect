package com.creatorconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyProfileRequest {
    @NotBlank
    private String companyName;
    private String logoUrl;
    private String website;
    private String industry;
    private String description;
    private String location;
    private String instagramHandle;
    private String linkedinHandle;
    private String twitterHandle;
}
