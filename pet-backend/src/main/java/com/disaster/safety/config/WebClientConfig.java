package com.disaster.safety.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${ai.eye.base-url:http://ai-eye:5000}")
    private String eyeBaseUrl;

    @Value("${ai.skin.base-url:http://ai-skin:5001}")
    private String skinBaseUrl;

    @Bean("eyeWebClient")
    public WebClient eyeWebClient() {
        return WebClient.builder()
                .baseUrl(eyeBaseUrl)
                .build();
    }

    @Bean("skinWebClient")
    public WebClient skinWebClient() {
        return WebClient.builder()
                .baseUrl(skinBaseUrl)
                .build();
    }
}
