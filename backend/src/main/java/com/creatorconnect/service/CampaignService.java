package com.creatorconnect.service;

import com.creatorconnect.dto.request.CampaignRequest;
import com.creatorconnect.dto.response.CampaignResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Platform;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CampaignService {
    CampaignResponse create(Long companyUserId, CampaignRequest request);
    CampaignResponse update(Long companyUserId, Long campaignId, CampaignRequest request);
    void delete(Long companyUserId, Long campaignId);
    CampaignResponse getById(Long campaignId, boolean incrementView);
    PageResponse<CampaignResponse> search(String keyword, String category, Platform platform,
                                           BigDecimal minBudget, BigDecimal maxBudget, Long minFollowers,
                                           LocalDate deadlineBefore, CampaignStatus status,
                                           int page, int size, String sortBy, String direction);
    PageResponse<CampaignResponse> getByCompany(Long companyUserId, int page, int size);
    PageResponse<CampaignResponse> getTrending(int page, int size);
    CampaignResponse updateStatus(Long companyUserId, Long campaignId, CampaignStatus status);
}
