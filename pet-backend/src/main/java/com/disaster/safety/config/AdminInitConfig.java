package com.disaster.safety.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.entity.RoleType;
import com.disaster.safety.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

// 2026-05-04: 실행시 관리자 계정 생성
@Configuration
@RequiredArgsConstructor
public class AdminInitConfig {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (memberRepository.findByUserId("admin").isEmpty()) {
                Member admin = Member.builder()
                        .userId("admin")
                        .userName("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(RoleType.ADMIN)
                        .build();

                memberRepository.create(admin);
            }
        };
    }
}
