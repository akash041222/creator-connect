package com.creatorconnect.service;

import com.creatorconnect.dto.request.ContentSubmissionRequest;
import com.creatorconnect.dto.request.SubmissionReviewRequest;
import com.creatorconnect.dto.response.ContentSubmissionResponse;

public interface ContentSubmissionService {
    ContentSubmissionResponse submit(Long creatorUserId, ContentSubmissionRequest request);
    ContentSubmissionResponse review(Long companyUserId, Long submissionId, SubmissionReviewRequest request);
    ContentSubmissionResponse getByApplication(Long applicationId);
}
