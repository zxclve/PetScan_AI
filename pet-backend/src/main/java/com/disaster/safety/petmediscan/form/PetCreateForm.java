package com.disaster.safety.petmediscan.form;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.disaster.safety.petmediscan.entity.Species;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetCreateForm {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotNull(message = "종은 필수 입력값입니다.")
    private Species species;

    private String breed;

    private LocalDateTime birth_date;

    @NotNull(message = "회원 ID는 필수 입력값입니다.")
    private Long memberId;
}
