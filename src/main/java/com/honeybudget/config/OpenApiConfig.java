package com.honeybudget.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [OpenAPI / Swagger 전역 설정]
 * API 문서 메타데이터 및 전역 설정을 정의합니다.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI honeyBudgetOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HoneyBudget API")
                        .description("내가쓰려고 만든 가계부 & 자산 관리 서비스 REST API 명세서")
                        .version("v1.0.0")
                        .contact(new Contact().name("HoneyBudget")));
    }
}
