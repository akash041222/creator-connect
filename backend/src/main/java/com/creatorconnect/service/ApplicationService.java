package com.creatorconnect.service;

import com.creatorconnect.dto.request.ApplicationRequest;
import com.creatorconnect.dto.request.ApplicationReviewRequest;
import com.creatorconnect.dto.response.ApplicationResponse;
import com.creatorconnect.dto.response.PageResponse;

public interface ApplicationService {
    ApplicationResponse apply(Long creatorUserId, ApplicationRequest request);
    ApplicationResponse review(Long companyUserId, Long applicationId, ApplicationReviewRequest request);
    ApplicationResponse withdraw(Long creatorUserId, Long applicationId);
    PageResponse<ApplicationResponse> getByCreator(Long creatorUserId, int page, int size);
    PageResponse<ApplicationResponse> getByCampaign(Long companyUserId, Long campaignId, int page, int size);
    PageResponse<ApplicationResponse> getByCompany(Long companyUserId, int page, int size);
    ApplicationResponse markCompleted(Long companyUserId, Long applicationId);
}
