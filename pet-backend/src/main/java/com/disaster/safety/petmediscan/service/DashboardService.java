package com.disaster.safety.petmediscan.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.petmediscan.dto.DashboardEventResponse;
import com.disaster.safety.petmediscan.dto.DashboardMetricResponse;
import com.disaster.safety.petmediscan.dto.DashboardSummaryResponse;
import com.disaster.safety.petmediscan.entity.DiagnosisLog;
import com.disaster.safety.petmediscan.entity.Disease;
import com.disaster.safety.petmediscan.entity.Types;
import com.disaster.safety.petmediscan.repository.DiagnosisLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DiagnosisLogRepository diagnosisLogRepository;

    public DashboardSummaryResponse buildSummary(Member member) {
        List<DiagnosisLog> logs = diagnosisLogRepository.findByMemberOrderByCreatedAtDesc(member);
        LocalDate today = LocalDate.now();

        List<DiagnosisLog> todayLogs = logs.stream()
                .filter(log -> log.getCreatedAt() != null && log.getCreatedAt().toLocalDate().equals(today))
                .toList();

        Map<Types, DiagnosisLog> latestByType = logs.stream()
                .filter(log -> log.getType() != null)
                .collect(Collectors.toMap(
                        DiagnosisLog::getType,
                        log -> log,
                        (left, right) -> left));

        DashboardMetricResponse eyeMetric = buildMetric("눈 상태", latestByType.get(Types.Eye), "눈·결막·각막 상태", "emerald");
        DashboardMetricResponse skinMetric = buildMetric("피부 상태", latestByType.get(Types.Skin), "피부 발적·염증·가려움 상태", "amber");

        List<DashboardEventResponse> recentEvents = logs.stream()
                .limit(5)
                .map(this::toEventResponse)
                .toList();

        int healthCondition = Math.max(0, Math.min(100, Math.round(
                (healthScore(latestByType.get(Types.Eye)) + healthScore(latestByType.get(Types.Skin))) / 2.0f)));

        return DashboardSummaryResponse.builder()
                .todayEventCount(todayLogs.size())
                .healthCondition(healthCondition)
                .healthStatus(healthCondition >= 80 ? "정상 운영" : healthCondition >= 60 ? "주의 필요" : "점검 필요")
                .healthMetrics(List.of(eyeMetric, skinMetric))
                .recentEvents(recentEvents)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private DashboardMetricResponse buildMetric(String name, DiagnosisLog log, String fallbackDetail, String tone) {
        if (log == null || log.getDisease() == null) {
            return DashboardMetricResponse.builder()
                    .name(name)
                    .value("데이터 없음")
                    .detail("아직 진단 기록이 없습니다.")
                    .tone("emerald")
                    .build();
        }

        Disease disease = log.getDisease();
        String value = isNormal(disease) ? "정상" : levelLabel(disease);
        String detail = isNormal(disease)
                ? fallbackDetail + " 정상"
                : disease.getKoreanName() + " 감지됨";

        return DashboardMetricResponse.builder()
                .name(name)
                .value(value)
                .detail(detail)
                .tone(isNormal(disease) ? "emerald" : tone)
                .build();
    }

    private DashboardEventResponse toEventResponse(DiagnosisLog log) {
        Disease disease = log.getDisease();
        String level = disease == null ? "주의" : levelLabel(disease);
        String type = disease == null ? "진단 이벤트" : disease.getKoreanName();
        if (log.getPet() != null) {
            type = log.getPet().getName() + " · " + type;
        }

        return DashboardEventResponse.builder()
                .time(log.getCreatedAt() == null ? "--:--" : log.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .site(log.getType() == Types.Eye ? "안구 분석" : "피부 분석")
                .type(type)
                .level(level)
                .build();
    }

    private int healthScore(DiagnosisLog log) {
        if (log == null || log.getDisease() == null) {
            return 100;
        }

        Disease disease = log.getDisease();
        if (isNormal(disease)) {
            return 100;
        }

        if (disease.getRiskLevel() == null) {
            return 65;
        }

        return switch (disease.getRiskLevel()) {
            case 상 -> 35;
            case 중 -> 65;
            case 하 -> 85;
        };
    }

    private String levelLabel(Disease disease) {
        if (disease == null) return "주의";
        if (isNormal(disease)) return "정상";
        if (disease.getRiskLevel() == null) return "주의";
        return switch (disease.getRiskLevel()) {
            case 상 -> "위험";
            case 중 -> "주의";
            case 하 -> "정상";
        };
    }

    private boolean isNormal(Disease disease) {
        if (disease == null || disease.getName() == null) {
            return false;
        }
        String name = disease.getName().toLowerCase();
        return name.contains("normal") || "정상".equals(disease.getKoreanName());
    }
}