package com.disaster.safety.petmediscan.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardMetricResponse {
    private String name;
    private String value;
    private String detail;
    private String tone;
}