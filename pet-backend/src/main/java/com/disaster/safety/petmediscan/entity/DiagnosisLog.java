package com.disaster.safety.petmediscan.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.disaster.safety.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "diagnosis_log")
@Getter 
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DiagnosisLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 질병인지 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Disease disease;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Member member;

    @Enumerated(EnumType.STRING)
    private Types type;

    private double score;

    private String imageUrl; // 선택

    private LocalDateTime createdAt = LocalDateTime.now();
}