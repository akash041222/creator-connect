package com.creatorconnect.service;

import com.creatorconnect.dto.response.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse creatorDashboard(Long creatorUserId);
    DashboardStatsResponse companyDashboard(Long companyUserId);
    DashboardStatsResponse adminDashboard();
}
