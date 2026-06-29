package com.disaster.safety.petmediscan.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "disease")
@Getter 
@Setter
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // AI label

    @Column(nullable = false)
    private String koreanName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String treatment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "risk_level")
    private RiskTypes riskLevel; // 상 / 중 / 하

    @Column(nullable = false, name = "category" )
    private String category; // Skin/Eye 또는 피부/안구
}