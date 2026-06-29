package com.disaster.safety.petmediscan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.disaster.safety.petmediscan.entity.VersionData;
import com.disaster.safety.petmediscan.service.VersionDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/versions")
public class VersionDataController {
    private final VersionDataService versionDataService;

    @GetMapping("")
    public ResponseEntity<List<VersionData>> versionList(@AuthenticationPrincipal UserDetails userDetails) {
        List<VersionData> pets = versionDataService.findAll()
                .stream()
                .toList();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{versionId}")
    public ResponseEntity<VersionData> getPet(@PathVariable("versionId") Long versionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(versionDataService.get(versionId));
    }
}
