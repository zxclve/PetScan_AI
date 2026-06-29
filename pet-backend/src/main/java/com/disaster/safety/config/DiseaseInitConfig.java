package com.disaster.safety.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.disaster.safety.petmediscan.entity.Disease;
import com.disaster.safety.petmediscan.entity.RiskTypes;
import com.disaster.safety.petmediscan.repository.DiseaseRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DiseaseInitConfig {

    private final DiseaseRepository diseaseRepository;

    @Bean
    public CommandLineRunner initDefaultDiseases() {
        return args -> {
            ensureDisease("eye-normal", "정상", "안구 상태가 정상 범위입니다.", "정기 검진을 유지하세요.", RiskTypes.하, "Eye");
            ensureDisease("skin-normal", "정상", "피부 상태가 정상 범위입니다.", "보습과 위생 관리를 유지하세요.", RiskTypes.하, "Skin");
        };
    }

    private void ensureDisease(String name, String koreanName, String description, String treatment, RiskTypes riskLevel, String category) {
        if (diseaseRepository.findByName(name).isPresent()) {
            return;
        }

        Disease disease = new Disease();
        disease.setName(name);
        disease.setKoreanName(koreanName);
        disease.setDescription(description);
        disease.setTreatment(treatment);
        disease.setRiskLevel(riskLevel);
        disease.setCategory(category);
        diseaseRepository.save(disease);
    }
}
