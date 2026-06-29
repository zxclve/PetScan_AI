package com.disaster.safety.petmediscan.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.entity.RoleType;
import com.disaster.safety.member.repository.MemberRepository;
import com.disaster.safety.petmediscan.entity.Pet;
import com.disaster.safety.petmediscan.entity.Species;
import com.disaster.safety.petmediscan.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    public Pet create(String name, Species species, Long memberId, String breed, LocalDateTime birthday) {
        Member member = memberRepository.findMember(memberId);
        if (member == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다. memberId=" + memberId);
        }

        Pet pet = new Pet();
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setBirth_date(birthday);
        pet.setMember(member);

        return petRepository.save(pet);
    }

    public List<Pet> findAllByMember(Long memberId) {
        Member member = memberRepository.findMember(memberId);
        if (member == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다. memberId=" + memberId);
        }

        return petRepository.findByMember(member);
    }

    public Pet get(long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다. id=" + id));
    }

    public Pet getAuthorizedPet(long id, Member loginMember) {
        // 2026-05-04: pet 접근은 소유자 또는 ADMIN만 허용
        Pet pet = get(id);
        validatePetAccess(pet, loginMember);
        return pet;
    }

    public void validateMemberAccess(Long requestedMemberId, Member loginMember) {
        // 2026-05-04: memberId 기반 요청도 본인 또는 ADMIN만 허용
        if (isAdmin(loginMember)) {
            return;
        }

        if (!loginMember.getId().equals(requestedMemberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
    }

    public Pet modify(long id, String breed, LocalDateTime birth) {
        Pet pet = get(id);
        pet.setBreed(breed);
        pet.setBirth_date(birth);

        return petRepository.save(pet);
    }

    public void delete(long id) {
        Pet pet = get(id);
        petRepository.delete(pet);
    }

    private void validatePetAccess(Pet pet, Member loginMember) {
        if (isAdmin(loginMember)) {
            return;
        }

        if (pet.getMember() == null || !loginMember.getId().equals(pet.getMember().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
    }

    private boolean isAdmin(Member member) {
        return member.getRole() == RoleType.ADMIN;
    }
}
