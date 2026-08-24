package com.creatorconnect.service.impl;

import com.creatorconnect.dto.response.DashboardStatsResponse;
import com.creatorconnect.entity.Company;
import com.creatorconnect.entity.Creator;
import com.creatorconnect.entity.enums.ApplicationStatus;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Role;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.repository.*;
import com.creatorconnect.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregates counts and lightweight time-series data for the three dashboard
 * types. Kept intentionally simple (in-memory aggregation over small result
 * sets) rather than complex native SQL, since dashboard data here is scoped
 * per-user and small; swap for materialized views if data volume grows.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CreatorRepository creatorRepository;
    private final CompanyRepository companyRepository;
    private final CampaignRepository campaignRepository;
    private final ApplicationRepository applicationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public DashboardStatsResponse creatorDashboard(Long creatorUserId) {
        Creator creator = creatorRepository.findByUserId(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found."));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalApplications", applicationRepository.findByCreatorId(creator.getId(), Pageable.unpaged()).getTotalElements());
        summary.put("pendingApplications", applicationRepository.countByCreatorIdAndStatus(creator.getId(), ApplicationStatus.PENDING));
        summary.put("acceptedApplications", applicationRepository.countByCreatorIdAndStatus(creator.getId(), ApplicationStatus.ACCEPTED));
        summary.put("completedCampaigns", creator.getCompletedCampaignsCount());
        summary.put("totalEarnings", creator.getTotalEarnings());
        summary.put("averageRating", creator.getAverageRating());
        summary.put("profileCompletion", creator.getProfileCompletionPercent());

        List<Map<String, Object>> recentActivity = activityLogRepository
                .findByUserIdOrderByCreatedAtDesc(creatorUserId, PageRequest.of(0, 10))
                .map(a -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("action", a.getAction());
                    m.put("description", a.getDescription());
                    m.put("timestamp", a.getCreatedAt());
                    return (Map<String, Object>) m;
                }).getContent();

        return DashboardStatsResponse.builder()
                .summary(summary)
                .chartSeries(List.of())
                .recentActivity(recentActivity)
                .build();
    }

    @Override
    public DashboardStatsResponse companyDashboard(Long companyUserId) {
        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("activeCampaigns", campaignRepository.findByCompanyId(company.getId(), PageRequest.of(0, 1)).getTotalElements());
        summary.put("totalApplications", applicationRepository.countByCampaignCompanyId(company.getId()));
        summary.put("totalSpending", company.getTotalSpending());
        summary.put("averageRating", company.getAverageRating());
        summary.put("totalCampaigns", company.getTotalCampaigns());

        List<Map<String, Object>> recentActivity = activityLogRepository
                .findByUserIdOrderByCreatedAtDesc(companyUserId, PageRequest.of(0, 10))
                .map(a -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("action", a.getAction());
                    m.put("description", a.getDescription());
                    m.put("timestamp", a.getCreatedAt());
                    return (Map<String, Object>) m;
                }).getContent();

        return DashboardStatsResponse.builder()
                .summary(summary)
                .chartSeries(List.of())
                .recentActivity(recentActivity)
                .build();
    }

    @Override
    public DashboardStatsResponse adminDashboard() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalUsers", userRepository.count());
        summary.put("totalCompanies", userRepository.countByRole(Role.COMPANY));
        summary.put("totalCreators", userRepository.countByRole(Role.CREATOR));
        summary.put("openCampaigns", campaignRepository.countByStatus(CampaignStatus.OPEN));
        summary.put("completedCampaigns", campaignRepository.countByStatus(CampaignStatus.COMPLETED));
        summary.put("totalPayments", paymentRepository.count());

        return DashboardStatsResponse.builder()
                .summary(summary)
                .chartSeries(List.of())
                .recentActivity(List.of())
                .build();
    }
}
