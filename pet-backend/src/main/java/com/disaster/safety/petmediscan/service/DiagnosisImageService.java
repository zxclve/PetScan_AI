package com.disaster.safety.petmediscan.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DiagnosisImageService {
    private static final Path STORAGE_DIR = Paths.get("uploads", "diagnosis").toAbsolutePath().normalize();

    public String saveAndGetUrl(MultipartFile file) throws IOException {
        Files.createDirectories(STORAGE_DIR);

        String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) {
            extension = original.substring(dot).toLowerCase(Locale.ROOT);
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path target = STORAGE_DIR.resolve(storedFileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/api/diagnosis/images/" + storedFileName;
    }

    public Resource loadAsResource(String fileName) throws MalformedURLException {
        Path filePath = STORAGE_DIR.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return null;
        }
        return resource;
    }
}