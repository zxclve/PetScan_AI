package com.disaster.safety.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    
    private Long id;
    private String userId;
    private String password;
    private String userName;

}
