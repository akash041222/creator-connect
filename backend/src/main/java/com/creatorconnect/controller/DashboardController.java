package com.creatorconnect.controller;

import com.creatorconnect.dto.response.DashboardStatsResponse;
import com.creatorconnect.service.DashboardService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboards", description = "Aggregated KPI data for each role's dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/creator")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<DashboardStatsResponse> creator() {
        return ResponseEntity.ok(dashboardService.creatorDashboard(SecurityUtil.currentUserId()));
    }

    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<DashboardStatsResponse> company() {
        return ResponseEntity.ok(dashboardService.companyDashboard(SecurityUtil.currentUserId()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsResponse> admin() {
        return ResponseEntity.ok(dashboardService.adminDashboard());
    }
}
