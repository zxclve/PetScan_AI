package com.disaster.safety.petmediscan.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.disaster.safety.petmediscan.entity.Disease;
import com.disaster.safety.petmediscan.entity.Types;
import com.disaster.safety.petmediscan.repository.DiseaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiseaseService { //DB매칭만 담당(Disease에는 질병명, 설명, 치료법, 위험도 등등이 담겨있음 - 로그가 아님!)
    private final DiseaseRepository diseaseRepository;

    // ai-skin 모델 라벨 → DB name 매핑 (A1_Papule_Plaque → Papule&plaque 등)
    private static final Map<String, String> SKIN_LABEL_MAP = Map.of(
        "a1_papule_plaque",     "Papule&plaque",
        "a2_dandruff_scale",    "Scale&Dandruff&EpidermalCollarette",
        "a3_lichenification",   "Lichenification&Hyperpigmentation",
        "a4_pustule",           "Pustule&Acne",
        "a5_erosion_ulcer",     "Erosion&Ulcer",
        "a6_nodule_mass",       "Nodule&mass",
        "a7_asymptomatic",      "skin-normal"
    );

    // ai-eye 모델 한국어 라벨 → DB name 매핑
    // _무 (질환 없음) 라벨은 normalizeLabels에서 별도 처리
    private static final Map<String, String> EYE_LABEL_MAP = Map.ofEntries(
        Map.entry("개_결막염_유",             "eye-normal"),      // DB에 결막염 없음
        Map.entry("개_궤양성각막질환_상",     "corneal_disease"),
        Map.entry("개_궤양성각막질환_하",     "corneal_disease"),
        Map.entry("개_ 백내장_비성숙",        "cataract"),        // 모델 라벨에 공백 포함
        Map.entry("개_백내장_비성숙",         "cataract"),
        Map.entry("개_백내장_성숙",           "cataract"),
        Map.entry("개_백내장_초기",           "cataract"),
        Map.entry("개_비궤양성각막질환_상",   "non_ulcerative_keratitis"),
        Map.entry("개_비궤양성각막질환_하",   "non_ulcerative_keratitis"),
        Map.entry("개_색소침착성각막염_유",   "pigmentary_keratitis"),
        Map.entry("개_안검내반증_유",         "entropion"),
        Map.entry("개_안검염_유",             "blepharitis"),
        Map.entry("개_안검종양_유",           "eyelid_tumor"),
        Map.entry("개_유루증_유",             "epiphora"),
        Map.entry("개_핵경화_유",             "nuclear_sclerosis"),
        Map.entry("고양이_각막궤양_유",       "corneal_ulcer"),
        Map.entry("고양이_각막부골편_유",     "corneal_sequestrum"),
        Map.entry("고양이_결막염_유",         "eye-normal"),      // DB에 결막염 없음
        Map.entry("고양이_비궤양성각막염_유", "non_ulcerative_keratitis"),
        Map.entry("고양이_안검염_유",         "blepharitis")
    );

    // label 리스트로 disease 조회
    public List<Disease> matchDiseases(List<String> labels, Types type) {
        List<String> normalizedLabels = normalizeLabels(labels, type);
        List<String> categories = type == Types.Eye
                ? List.of("Eye", "안구")
                : List.of("Skin", "피부");

        List<Disease> matched = diseaseRepository.findByNameInAndCategoryIn(normalizedLabels, categories);
        if (!matched.isEmpty()) {
            return matched;
        }

        String fallbackName = type == Types.Eye ? "eye-normal" : "skin-normal";
        return diseaseRepository.findByNameInAndCategoryIn(List.of(fallbackName), categories);
    }

    private List<String> normalizeLabels(List<String> labels, Types type) {
        String normalLabel = type == Types.Eye ? "eye-normal" : "skin-normal";
        if (labels == null || labels.isEmpty()) {
            return List.of(normalLabel);
        }
        return labels.stream()
                .filter(label -> label != null && !label.isBlank())
                .map(label -> normalizeLabelToDbName(label, type))
                .distinct()
                .toList();
    }

    // FastAPI 라벨 → DB name 정규화 (단일 라벨용, scoreMap 키 생성에도 사용)
    public String normalizeLabelToDbName(String label, Types type) {
        if (label == null || label.isBlank()) return label;
        String trimmed = label.trim();
        if ("normal".equalsIgnoreCase(trimmed)) {
            return type == Types.Eye ? "eye-normal" : "skin-normal";
        }
        // Eye: _무 suffix (질환 없음) → eye-normal, 이후 EYE_LABEL_MAP 조회
        if (type == Types.Eye) {
            if (trimmed.endsWith("_무")) return "eye-normal";
            if (EYE_LABEL_MAP.containsKey(trimmed)) return EYE_LABEL_MAP.get(trimmed);
        }
        String lower = trimmed.toLowerCase(Locale.ROOT);
        if (type == Types.Skin && SKIN_LABEL_MAP.containsKey(lower)) {
            return SKIN_LABEL_MAP.get(lower);
        }
        if (lower.equals("skin_normal")) return "skin-normal";
        if (lower.equals("eye_normal"))  return "eye-normal";
        return trimmed;
    }

    // label 하나로 disease 조회
    public Disease matchDisease(String label) {
        return diseaseRepository.findByName(label)
                .orElseThrow(() -> new RuntimeException("질병 정보 없음: " + label));
    }
}
