package com.disaster.safety.petmediscan.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.service.MemberService;
import com.disaster.safety.petmediscan.dto.DiseaseResponse;
import com.disaster.safety.petmediscan.entity.Diagnosis;
import com.disaster.safety.petmediscan.entity.DiagnosisLog;
import com.disaster.safety.petmediscan.entity.Pet;
import com.disaster.safety.petmediscan.entity.Types;
import com.disaster.safety.petmediscan.service.DiagnosisImageService;
import com.disaster.safety.petmediscan.service.DiagnosisLogService;
import com.disaster.safety.petmediscan.service.DiagnosisService;
import com.disaster.safety.petmediscan.service.PetService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/diagnosis")
public class DiagnosisController {
    private final DiagnosisService diagnosisService;
    private final PetService petService;
    private final MemberService memberService;
    private final DiagnosisImageService diagnosisImageService;
    private final DiagnosisLogService diagnosisLogService;

    @PostMapping("/skin")
    public String setSkin(Integer petId, String image,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: 피부 진단 전 pet 접근 권한 확인
        Member member = memberService.getByUserId(userDetails.getUsername());
        Pet pet = petService.getAuthorizedPet(petId, member);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPet(pet);
        diagnosis.setType(Types.Skin);
        diagnosis.setResult("custom");
        diagnosis.setImage_url(image);
        diagnosis.setConfidence(null);
        diagnosis.setDiagnosis_date(LocalDateTime.now());
        return "";
    }

    @PostMapping("/eye")
    public String setEye(Integer petId, String image,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: 안구 진단 전 pet 접근 권한 확인
        Member member = memberService.getByUserId(userDetails.getUsername());
        Pet pet = petService.getAuthorizedPet(petId, member);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPet(pet);
        diagnosis.setType(Types.Eye);
        diagnosis.setResult("custom");
        diagnosis.setImage_url(image);
        diagnosis.setConfidence(null);
        diagnosis.setDiagnosis_date(LocalDateTime.now());
        return "";
    }

    @PostMapping
    public ResponseEntity<List<DiseaseResponse>> diagnose(
            @RequestPart("image") MultipartFile file,
            @RequestParam("type") Types type,
            @RequestParam("petId") Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 2026-05-04: 진단 요청은 pet 소유자 또는 ADMIN만 허용
            Member member = memberService.getByUserId(userDetails.getUsername());
            Pet pet = petService.getAuthorizedPet(petId, member);

            return ResponseEntity.ok(diagnosisService.diagnose(file, type, pet, member));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/events/{petId}")
    public ResponseEntity<List<DiagnosisLog>> history(@PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: 진단 이력 조회 전 pet 접근 권한 검증
        Member member = memberService.getByUserId(userDetails.getUsername());
        Pet pet = petService.getAuthorizedPet(petId, member);
        return ResponseEntity.ok(diagnosisLogService.getLogsByPet(pet));
    }

    @GetMapping("/events")
    public ResponseEntity<List<DiagnosisLog>> historyByUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.getByUserId(userDetails.getUsername());
        return ResponseEntity.ok(diagnosisLogService.getLogsByMember(member));
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getDiagnosisImage(@PathVariable String fileName) {
        try {
            Resource resource = diagnosisImageService.loadAsResource(fileName);
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
