package com.disaster.safety.member.dto;


import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    @NotNull(message = "Id 입력은 필수입니다.")
    private String userId;

    @NotNull(message = "password 입력은 필수입니다.")
    private String password;
}
