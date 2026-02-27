package com.tramtruyen.api.infrastructure.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. Khai báo nút Authorize (Ổ khóa) dùng chuẩn Bearer Token (JWT)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                // 2. Yêu cầu toàn bộ API phải áp dụng cái ổ khóa này
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 3. Thông tin chung của dự án hiển thị trên trang chủ Swagger
                .info(new Info()
                        .title("Trạm Truyện API")
                        .description("Tài liệu API cho dự án Trạm Truyện tích hợp Spring Security & JWT")
                        .version("v1.0.0")
                );
    }
}