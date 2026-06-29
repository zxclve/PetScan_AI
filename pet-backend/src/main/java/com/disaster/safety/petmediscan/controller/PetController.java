package com.disaster.safety.petmediscan.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.service.MemberService;
import com.disaster.safety.petmediscan.dto.PetResponseDto;
import com.disaster.safety.petmediscan.form.PetCreateForm;
import com.disaster.safety.petmediscan.service.PetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets")
public class PetController {
    private final PetService petService;
    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<List<PetResponseDto>> petList(@RequestParam("memberId") Long memberId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: pet 목록 조회 전 로그인 사용자 소유권 확인
        Member loginMember = memberService.getByUserId(userDetails.getUsername());
        petService.validateMemberAccess(memberId, loginMember);

        List<PetResponseDto> pets = petService.findAllByMember(memberId)
                .stream()
                .map(PetResponseDto::from)
                .toList();
        return ResponseEntity.ok(pets);
    }

    @PostMapping("")
    public ResponseEntity<PetResponseDto> createPet(@Valid @RequestBody PetCreateForm petCreateForm,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: pet 생성도 요청 memberId와 로그인 사용자를 대조
        Member loginMember = memberService.getByUserId(userDetails.getUsername());
        petService.validateMemberAccess(petCreateForm.getMemberId(), loginMember);

        String breed = petCreateForm.getBreed() == null ? "" : petCreateForm.getBreed();
        var pet = petService.create(
                petCreateForm.getName(),
                petCreateForm.getSpecies(),
                petCreateForm.getMemberId(),
                breed,
                petCreateForm.getBirth_date());

        return ResponseEntity.status(HttpStatus.CREATED).body(PetResponseDto.from(pet));
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponseDto> getPet(@PathVariable("petId") Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: pet 단건 조회는 소유자 또는 ADMIN만 허용
        Member loginMember = memberService.getByUserId(userDetails.getUsername());
        return ResponseEntity.ok(PetResponseDto.from(petService.getAuthorizedPet(petId, loginMember)));
    }

    @PutMapping("/{petId}")
    public ResponseEntity<PetResponseDto> modifyPet(@RequestBody PetCreateForm petCreateForm,
            @PathVariable("petId") Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: pet 수정 전 접근 권한 먼저 검증
        Member loginMember = memberService.getByUserId(userDetails.getUsername());
        petService.getAuthorizedPet(petId, loginMember);

        var pet = petService.modify(petId, petCreateForm.getBreed(), petCreateForm.getBirth_date());
        return ResponseEntity.ok(PetResponseDto.from(pet));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable("petId") Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 2026-05-04: pet 삭제 전 접근 권한 먼저 검증
        Member loginMember = memberService.getByUserId(userDetails.getUsername());
        petService.getAuthorizedPet(petId, loginMember);

        petService.delete(petId);
        return ResponseEntity.noContent().build();
    }
}
