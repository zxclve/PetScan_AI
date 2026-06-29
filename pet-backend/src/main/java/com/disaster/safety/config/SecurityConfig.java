package com.disaster.safety.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.disaster.safety.member.service.CustomOauth2UserService;
import com.disaster.safety.member.service.CustomUserDetailsService;
import com.disaster.safety.security.filter.JwtAuthFilter;
import com.disaster.safety.security.handler.CustomAccessDeniedHandler;
import com.disaster.safety.security.handler.CustomAuthenticationEntryPoint;
import com.disaster.safety.security.handler.OAuth2LoginFailureHandler;
import com.disaster.safety.security.util.JwtUtil;


import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;


    private static final String[] AUTH_WHITELIST = {
            "/api/member/login",
            "/api/member/signup",
            "/h2-console/**",
            "/swagger-ui/**",
            //"/api-docs",
            //"/swagger-ui-custom.html"
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            // 스웨거 로컬 접속주소 
            // http://localhost:8080/swagger-ui/index.html

            "/oauth2/authorization/**",
            "/login/oauth2/code/**",
            "/api/member/login/oauth2/**"
            // 소셜 로그인 테스트 주소
            // http://localhost:8080/oauth2/authorization/google\
            // 승인되고 나서 테스트 할 주소
            // http://localhost:8080/login/oauth2/code/google
        
            
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.headers(headers -> headers.frameOptions().disable());

        //http.sessionManagement(sessionManagement ->
                //sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // OAuth2 로그인 방식 설정
        http.oauth2Login(authorizeRequests -> authorizeRequests
                .userInfoEndpoint(userInfo -> userInfo.userService(customOauth2UserService))
                .defaultSuccessUrl("/api/member/login/oauth2/success", true)
                .failureHandler(oAuth2LoginFailureHandler));

        // 로그아웃
        http.logout((authorizeRequests) -> authorizeRequests.logoutUrl("/api/member/logout"));
                

        http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));

        http.authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated());

        return http.build();
    }
}
