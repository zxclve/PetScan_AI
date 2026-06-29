package com.disaster.safety.petmediscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.service.MemberService;
import com.disaster.safety.petmediscan.dto.DashboardSummaryResponse;
import com.disaster.safety.petmediscan.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final MemberService memberService;
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> summary(@AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.getByUserId(userDetails.getUsername());
        return ResponseEntity.ok(dashboardService.buildSummary(member));
    }
}