package com.disaster.safety.member.dto;

import com.disaster.safety.member.entity.RoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로직 내부에서 인증 유저 정보를 저장
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserInfoDto extends MemberDto {
    
    private Long memberId;
    private String userId;
    private String password;
    private String userName;
    private RoleType role;

}
