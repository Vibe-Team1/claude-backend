package com.example.claude_backend.presentation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 3.0 설정 API 문서 자동 생성을 위한 설정
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Configuration
public class SwaggerConfig {

  @Value("${spring.application.name}")
  private String applicationName;

  /** OpenAPI 설정 */
  @Bean
  public OpenAPI openAPI() {
    // JWT 보안 스키마 정의
    SecurityScheme securityScheme =
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

    // 보안 요구사항
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
        .info(apiInfo())
        .servers(getServers())
        .addSecurityItem(securityRequirement)
        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
  }

  /** API 정보 설정 */
  private Info apiInfo() {
    return new Info()
        .title("StockRoomSNS API")
        .description(
            """
                StockRoomSNS - SNS형 모의투자 서비스 API

                ## API 버전
                현재 API 버전: v1
                - 모든 API 엔드포인트는 `/api/v1/`로 시작합니다
                - 향후 v2, v3 버전 확장 예정

                ## 주요 기능
                - Google OAuth2 로그인
                - 사용자 프로필 관리
                - 모의투자 (추후 구현)
                - SNS 룸 꾸미기 (추후 구현)

                ## 인증
                모든 API는 JWT Bearer 토큰이 필요합니다.
                1. Google OAuth2로 로그인
                2. 발급받은 JWT 토큰을 Authorization 헤더에 포함
                   `Authorization: Bearer {token}`

                ## API 엔드포인트 예시
                - 사용자 정보: `/api/v1/users/me`
                - 친구 목록: `/api/v1/friends`
                - 상점 뽑기: `/api/v1/shop/draw`
                - 커스터마이제이션: `/api/v1/user/customization`
                """)
        .version("v1.0.0")
        .contact(
            new Contact()
                .name("StockRoomSNS Team")
                .email("support@stockroom-sns.com")
                .url("https://github.com/Vibe-Team1/claude-backend"))
        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"));
  }

  /** 서버 목록 설정 */
  private List<Server> getServers() {
    Server finlandServer = new Server().url("http://finland.r-e.kr:8080").description("Finland 서버");

    Server localServer = new Server().url("http://localhost:8080").description("로컬 개발 서버");

    Server devServer = new Server().url("https://dev.stockroom-sns.com").description("개발 서버");

    Server prodServer = new Server().url("https://api.stockroom-sns.com").description("프로덕션 서버");

    return List.of(finlandServer, localServer, devServer, prodServer);
  }
}
