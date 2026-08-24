package com.creatorconnect.service;

import com.creatorconnect.dto.request.CreatorProfileRequest;
import com.creatorconnect.dto.response.CreatorProfileResponse;
import com.creatorconnect.dto.response.PageResponse;

public interface CreatorService {
    CreatorProfileResponse getProfile(Long userId);
    CreatorProfileResponse getPublicProfile(Long creatorId);
    CreatorProfileResponse updateProfile(Long userId, CreatorProfileRequest request);
    PageResponse<CreatorProfileResponse> search(String category, String location, Long minFollowers, int page, int size);
    PageResponse<CreatorProfileResponse> leaderboard(int page, int size);
}
