package com.creatorconnect.service;

import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.dto.response.UserResponse;
import com.creatorconnect.entity.enums.Role;

public interface AdminService {
    PageResponse<UserResponse> listUsers(Role role, int page, int size);
    void suspendUser(Long userId);
    void reactivateUser(Long userId);
    void deleteUser(Long userId);
    void verifyCompany(Long companyId);
    void verifyCreator(Long creatorId);
    void deleteCampaign(Long campaignId);
}
