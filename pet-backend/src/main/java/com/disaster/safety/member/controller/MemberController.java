package com.disaster.safety.member.controller;

//import java.util.HashMap;
//import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disaster.safety.member.details.CustomOauth2UserDetails;
import com.disaster.safety.member.dto.CustomUserInfoDto;
import com.disaster.safety.member.dto.LoginRequestDto;
import com.disaster.safety.member.dto.MemberRequestDto;
import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.service.MemberService;
import com.disaster.safety.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> getMemberProfile(@Valid @RequestBody LoginRequestDto request) {
        String token = memberService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@Valid @RequestBody MemberRequestDto member) {
        // 2026-05-04: 회원가입 role은 서버에서만 부여하도록 변경
        Member entity = new Member();
        entity.setUserId(member.getUserId());
        entity.setUserName(member.getUserName());
        entity.setPassword(member.getPassword());

        Long id = memberService.signup(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/login/oauth2/success")
    public ResponseEntity<Void> oauth2Success(Authentication authentication) {
        CustomOauth2UserDetails oAuth2UserDetails = (CustomOauth2UserDetails) authentication.getPrincipal();
        Member member = oAuth2UserDetails.getMember();

        CustomUserInfoDto info = CustomUserInfoDto.builder()
                .memberId(member.getId())
                .userId(member.getUserId())
                .password(member.getPassword())
                .userName(member.getUserName())
                .role(member.getRole())
                .build();

        String accessToken = jwtUtil.createAccessToken(info);
        // OAuth2 로그인 성공 시 클라이언트로 토큰과 사용자 정보 전달
        String redirectUrl = "http://localhost:5173/oauth/callback?accessToken=" + accessToken;
        // 프론트 콜백 페이지로 리다이렉트
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();
    }

    @GetMapping("/login/oauth2/failure")
    public ResponseEntity<String> oauth2Failure(@RequestParam(required = false) String message) {
        String errorMessage = message == null ? "OAuth2 login failed" : "OAuth2 login failed: " + message;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
