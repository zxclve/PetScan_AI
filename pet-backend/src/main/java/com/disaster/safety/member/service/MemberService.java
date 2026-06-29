package com.disaster.safety.member.service;

import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.disaster.safety.member.dto.CustomUserInfoDto;
import com.disaster.safety.member.dto.LoginRequestDto;
import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.entity.RoleType;
import com.disaster.safety.member.exception.ValidateMemberException;
import com.disaster.safety.member.repository.MemberRepository;
import com.disaster.safety.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    @Transactional
    public String login(LoginRequestDto dto) {
        String password = dto.getPassword();
        String userId = dto.getUserId();
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);

        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("User not found.");
        }

        Member member = optionalMember.get();

        if (!encoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException("Password does not match.");
        }

        CustomUserInfoDto info = CustomUserInfoDto.builder()
                .memberId(member.getId())
                .userId(member.getUserId())
                .password(member.getPassword())
                .userName(member.getUserName())
                .role(member.getRole())
                .build();
        return jwtUtil.createAccessToken(info);
    }

    @Transactional
    public Long signup(Member member) {
        Optional<Member> validMember = memberRepository.findByUserId(member.getUserId());

        if (validMember.isPresent()) {
            throw new ValidateMemberException("User already exists.");
        }

        member.updatePassword(encoder.encode(member.getPassword()));
        // 2026-05-04: 일반 회원가입 사용자는 기본 role을 USER로 고정
        member.setRole(RoleType.USER);
        memberRepository.create(member);
        return member.getId();
    }

    public Member getByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
