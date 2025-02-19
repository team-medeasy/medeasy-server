package com.medeasy.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // 보안 형식 생성
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Auth"); // 보안 요구사항 등록

        return new OpenAPI()
                .components(new Components())
                .info(info())
                .schemaRequirement("Bearer Auth", securityScheme) // 보안 형식에 이름 설정
                .addSecurityItem(securityRequirement)
                ;
    }

    private Info info() {
        return new Info()
                .title("Medeasy Basic API")
                .description("Medeasy API reference for developers")
                .version("1.0");
    }
}
