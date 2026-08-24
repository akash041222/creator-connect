package com.creatorconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Generic, reusable stats envelope for creator / company / admin dashboards. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Map<String, Object> summary;          // headline KPI cards
    private List<Map<String, Object>> chartSeries; // time-series data for Chart.js
    private List<Map<String, Object>> recentActivity;
}
