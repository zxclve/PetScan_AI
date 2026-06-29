package com.disaster.safety.security.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.disaster.safety.member.service.CustomUserDetailsService;
import com.disaster.safety.security.util.JwtUtil;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;


// 스프링 시큐리티 필터에 요청이 올때마다 JWT 유효성을 검사
// 유효시 사용자 정보를 SecurityContext에 설정
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    

    @Override
	protected void doFilterInternal(final HttpServletRequest request,
		final HttpServletResponse response,
		final FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");

        // JWT 헤더 존재시
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            // JWT 유효성 검사
            if (jwtUtil.isValidToken(token)) {
                String userId = jwtUtil.getUserId(token);

                // 유저와 토큰 일치 하면 userDetails 생성
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
                
                if (userDetails != null) {
                    //UserDetails, Password, Role -> 접근 권한 인증 Token 생성
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                        //현재 Request의 Security Context에 접근 권한 설정
                        SecurityContextHolder.getContext()
                            .setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }

            filterChain.doFilter(request, response); //다음 필터로 넘김
	}
}