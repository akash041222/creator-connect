package com.creatorconnect.service;

import com.creatorconnect.dto.request.CompanyProfileRequest;
import com.creatorconnect.dto.response.CompanyProfileResponse;
import com.creatorconnect.dto.response.PageResponse;

public interface CompanyService {
    CompanyProfileResponse getProfile(Long userId);
    CompanyProfileResponse getPublicProfile(Long companyId);
    CompanyProfileResponse updateProfile(Long userId, CompanyProfileRequest request);
    PageResponse<CompanyProfileResponse> search(String name, int page, int size);
}
