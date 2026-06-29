package com.disaster.safety.petmediscan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.petmediscan.entity.DiagnosisLog;
import com.disaster.safety.petmediscan.entity.Pet;

public interface DiagnosisLogRepository  extends JpaRepository<DiagnosisLog, Long> {
    // 특정 펫의 진단 기록 최신순
    List<DiagnosisLog> findByPetOrderByCreatedAtDesc(Pet pet);
    
    // 특정 멤버의 전체 진단 기록 최신순
    List<DiagnosisLog> findByMemberOrderByCreatedAtDesc(Member member);   
}
