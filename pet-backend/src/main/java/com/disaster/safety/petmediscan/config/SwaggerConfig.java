package com.disaster.safety.petmediscan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI petMediScanAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("PetMediScan API")
                .version("1.0.0")
                .description("반려동물 안구/피부질환 진단 REST API\n\n" +
                           "## 주요 기능\n" +
                           "- 사용자 인증 (JWT)\n" +
                           "- 반려동물 관리\n" +
                           "- 안구/피부 질환 진단\n" +
                           "- 진단 이력 조회")
                .contact(new Contact()
                    .name("KoreatIT125 Team")
                    .url("https://github.com/KoreatIT125")
                    .email("contact@petmediscan.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("개발 서버"),
                new Server()
                    .url("http://production-server:8080")
                    .description("프로덕션 서버")
            ));
    }
}
