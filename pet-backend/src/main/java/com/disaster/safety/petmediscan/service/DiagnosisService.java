package com.disaster.safety.petmediscan.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.petmediscan.dto.DiseaseResponse;
import com.disaster.safety.petmediscan.dto.FastApiResponse;
import com.disaster.safety.petmediscan.entity.Disease;
import com.disaster.safety.petmediscan.entity.Pet;
import com.disaster.safety.petmediscan.entity.Types;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiagnosisService {
    private final ImageService imageService;
        private final DiagnosisImageService diagnosisImageService;
    private final FastApiService fastApiService;
    private final DiseaseService diseaseService;
    private final DiagnosisLogService diagnosisLogService;

    public List<DiseaseResponse> diagnose(MultipartFile file, Types type, 
                                       Pet pet, Member member) throws IOException {
                String imageUrl = diagnosisImageService.saveAndGetUrl(file);

        // 1. 리사이징
        byte[] resizedImage = imageService.resizeImage(file);

        // 2. FastAPI 호출
        FastApiResponse response = fastApiService.predict(resizedImage, file.getOriginalFilename(), type);

        // 3. label 추출
        List<String> labels = response.getTop5().stream()
                .map(FastApiResponse.TopItem::getLabel)
                .distinct()
                .toList();

        // 4. DB 매칭
        List<Disease> diseases = diseaseService.matchDiseases(labels, type);

        // 5. score 매핑 (키를 DB name으로 정규화하여 매칭)
        Map<String, Double> scoreMap = response.getTop5().stream()
                .collect(Collectors.toMap(
                        item -> diseaseService.normalizeLabelToDbName(item.getLabel(), type),
                        FastApiResponse.TopItem::getScore,
                        (a, b) -> a
                ));

        // 6. 로그 저장
        diagnosisLogService.saveLogs(diseases, scoreMap, pet, member, type, imageUrl);

        // 7. 응답 반환
        return diseases.stream()
                .map(d -> new DiseaseResponse(
                        d.getName(),
                        d.getKoreanName(),
                        d.getDescription(),
                        d.getTreatment(),
                        d.getRiskLevel(),
                        scoreMap.getOrDefault(d.getName(), 0.0)
                ))
                .toList();
        }
}