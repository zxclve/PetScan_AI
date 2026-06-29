package com.disaster.safety.petmediscan.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.disaster.safety.petmediscan.entity.VersionData;
import com.disaster.safety.petmediscan.repository.VersionDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VersionDataService {
    private final VersionDataRepository versionDataRepository;

    public VersionData create(String version, String content) {
        VersionData versionData = new VersionData();
        versionData.setCurrentVersion(version);
        versionData.setVersionContent(content);

        return versionDataRepository.save(versionData);
    }

    public List<VersionData> findAll() {
        
        return versionDataRepository.findAll();
    }

    public VersionData get(long id) {
        return versionDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 버전을 찾을 수 없습니다. id=" + id));
    }
}
