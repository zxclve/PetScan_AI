package com.disaster.safety.petmediscan.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.petmediscan.entity.DiagnosisLog;
import com.disaster.safety.petmediscan.entity.Disease;
import com.disaster.safety.petmediscan.entity.Pet;
import com.disaster.safety.petmediscan.entity.Types;
import com.disaster.safety.petmediscan.repository.DiagnosisLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiagnosisLogService {
    private final DiagnosisLogRepository diagnosisLogRepository;

  public void saveLogs(List<Disease> diseases, Map<String, Double> scoreMap,
                         Pet pet, Member member, Types type, String imageUrl) {
        List<DiagnosisLog> logs = diseases.stream()
                .map(disease -> {
                    DiagnosisLog log = new DiagnosisLog();
                    log.setDisease(disease);
                    log.setScore(scoreMap.getOrDefault(disease.getName(), 0.0));
                    log.setPet(pet);
                    log.setMember(member);
                    log.setType(type);
                    log.setImageUrl(imageUrl);
                    return log;
                })
                .toList();


        diagnosisLogRepository.saveAll(logs);
    }

    // 펫 진단 기록 조회
    public List<DiagnosisLog> getLogsByPet(Pet pet) {
        return diagnosisLogRepository.findByPetOrderByCreatedAtDesc(pet);
    }

    public List<DiagnosisLog> getLogsByMember(Member member) {
        return diagnosisLogRepository.findByMemberOrderByCreatedAtDesc(member);
    }
}