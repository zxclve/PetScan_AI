package com.disaster.safety.petmediscan.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardEventResponse {
    private String time;
    private String site;
    private String type;
    private String level;
}