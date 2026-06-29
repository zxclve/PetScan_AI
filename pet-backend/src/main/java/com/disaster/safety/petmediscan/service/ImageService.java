package com.disaster.safety.petmediscan.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    private static final int TARGET_SIZE = 1280;
    // YOLOv5 letterbox 패딩 색상 (학습 시 사용한 값과 동일)
    private static final Color PAD_COLOR = new Color(114, 114, 114);

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/pjpeg",
            "image/gif",
            "image/bmp",
            "image/x-ms-bmp",
            "image/webp",
            "image/tiff"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "png", "jpg", "jpeg", "jfif", "gif", "bmp", "webp", "tif", "tiff"
    );

    public byte[] resizeImage(MultipartFile file) throws IOException {
        validateImageFile(file);

        BufferedImage src;
        try {
            src = ImageIO.read(file.getInputStream());
        } catch (Exception e) {
            throw new IllegalArgumentException("지원되지 않거나 손상된 이미지 파일입니다.", e);
        }
        if (src == null) {
            throw new IllegalArgumentException("지원되지 않거나 손상된 이미지 파일입니다.");
        }

        // YOLOv5 letterbox: 비율 유지 리사이즈 후 회색 패딩으로 정사각형 만들기
        BufferedImage letterboxed = letterbox(src, TARGET_SIZE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(letterboxed, "jpg", outputStream);
        return outputStream.toByteArray();
    }

    private BufferedImage letterbox(BufferedImage src, int targetSize) {
        int origW = src.getWidth();
        int origH = src.getHeight();

        // 장변 기준으로 scale 계산
        double scale = Math.min((double) targetSize / origW, (double) targetSize / origH);
        int scaledW = (int) Math.round(origW * scale);
        int scaledH = (int) Math.round(origH * scale);

        // 패딩 계산 (상하/좌우 균등 분배)
        int padX = (targetSize - scaledW) / 2;
        int padY = (targetSize - scaledH) / 2;

        BufferedImage canvas = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setColor(PAD_COLOR);
        g.fillRect(0, 0, targetSize, targetSize);
        g.drawImage(src, padX, padY, scaledW, scaledH, null);
        g.dispose();

        return canvas;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String contentType = normalizeContentType(file.getContentType());
        String extension = extractExtension(file.getOriginalFilename());

        boolean contentTypeAllowed = contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType);
        boolean extensionAllowed = extension != null && ALLOWED_EXTENSIONS.contains(extension);

        if (!contentTypeAllowed && !extensionAllowed) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return null;
        }

        String normalized = contentType.toLowerCase(Locale.ROOT).trim();
        int semicolonIndex = normalized.indexOf(';');
        if (semicolonIndex >= 0) {
            normalized = normalized.substring(0, semicolonIndex).trim();
        }

        return normalized;
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return null;
        }

        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}