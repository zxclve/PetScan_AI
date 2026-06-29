package com.disaster.safety.petmediscan.dto;

import java.time.LocalDateTime;

import com.disaster.safety.petmediscan.entity.Pet;
import com.disaster.safety.petmediscan.entity.Species;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetResponseDto {
    private Long id;
    private String name;
    private Species species;
    private String breed;
    private LocalDateTime birth_date;
    private Long memberId;

    public static PetResponseDto from(Pet pet) {
        return PetResponseDto.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .birth_date(pet.getBirth_date())
                .memberId(pet.getMember() == null ? null : pet.getMember().getId())
                .build();
    }
}
