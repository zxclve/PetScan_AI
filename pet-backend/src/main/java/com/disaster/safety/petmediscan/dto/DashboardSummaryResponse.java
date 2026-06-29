package com.disaster.safety.petmediscan.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {
    private int todayEventCount;
    private int healthCondition;
    private String healthStatus;
    private List<DashboardMetricResponse> healthMetrics;
    private List<DashboardEventResponse> recentEvents;
    private LocalDateTime updatedAt;
}