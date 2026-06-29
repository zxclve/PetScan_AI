package com.disaster.safety.security.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.disaster.safety.member.dto.CustomUserInfoDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

// JWT 토큰 생성 및 검증 유틸
@Slf4j
@Component
public class JwtUtil {
    // 서명 키와 Access Token 만료 시간
    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") final String secretKey,
            @Value("${jwt.expiration_time:3600}") final long accessTokenExpTime) {
        // 설정된 secret 문자열로 HMAC 서명 키 생성
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        System.out.println("JWT SECRET = " + secretKey);
        System.out.println("JWT SECRET LENGTH = " + secretKey.length());

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    // Access Token 생성
    public String createAccessToken(CustomUserInfoDto member) {
        return createToken(member, accessTokenExpTime);
    }

    // 민감한 비밀번호는 제외하고 JWT 생성
    private String createToken(CustomUserInfoDto member, long expireTime) {
        // 2026-05-04: JWT에는 password를 넣지 않고 role만 포함
        Claims claims = Jwts.claims();
        claims.put("memberId", member.getMemberId());
        claims.put("userId", member.getUserId());
        claims.put("userName", member.getUserName());
        claims.put("role", member.getRole().getValue());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 userId 추출
    public String getUserId(String token) {
        return parseClaims(token).get("userId", String.class);
    }

    // JWT 유효성 검증
    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    // 만료된 토큰이어도 Claims는 읽을 수 있도록 처리
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
