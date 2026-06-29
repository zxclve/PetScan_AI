package com.disaster.safety.petmediscan.dto;

import com.disaster.safety.petmediscan.entity.RiskTypes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiseaseResponse {
    private String name;
    private String koreanName;
    private String description;
    private String treatment;
    private RiskTypes  riskLevel;
    private double score;
}
