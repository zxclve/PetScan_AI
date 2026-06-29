package com.disaster.safety.member.details;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.disaster.safety.member.dto.CustomUserInfoDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Spring Security에서 인증된 사용자 정보를 담아두는 UserDetails 구현체

@Getter
@RequiredArgsConstructor
public class CustomSecurityUserDetails implements UserDetails {

    private final CustomUserInfoDto member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 2026-05-04: 일반 로그인 권한도 ROLE_* 형식으로 통일
        return List.of(new SimpleGrantedAuthority(member.getRole().getValue()));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
