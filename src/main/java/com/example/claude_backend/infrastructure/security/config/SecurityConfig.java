package com.example.claude_backend.infrastructure.security.config;

import com.example.claude_backend.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.example.claude_backend.infrastructure.security.oauth2.OAuth2FailureHandler;
import com.example.claude_backend.infrastructure.security.oauth2.OAuth2SuccessHandler;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 설정 OAuth2 로그인, JWT 인증, CORS 설정 등을 관리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final OAuth2SuccessHandler oauth2SuccessHandler;
  private final OAuth2FailureHandler oauth2FailureHandler;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      OAuth2SuccessHandler oauth2SuccessHandler,
      OAuth2FailureHandler oauth2FailureHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.oauth2SuccessHandler = oauth2SuccessHandler;
    this.oauth2FailureHandler = oauth2FailureHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 비활성화 (JWT 사용)
        .csrf(AbstractHttpConfigurer::disable)

        // CORS 설정
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // 세션 관리 (JWT 사용으로 인해 STATELESS)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 요청별 권한 설정
        .authorizeHttpRequests(
            authz ->
                authz
                    // 공개 엔드포인트
                    .requestMatchers("/")
                    .permitAll()
                    .requestMatchers("/api/v1/home/**")
                    .permitAll()
                    .requestMatchers("/api/v1/stocks/**")
                    .permitAll()
                    .requestMatchers("/api/v1/auth/**")
                    .permitAll()
                    .requestMatchers("/oauth2/**")
                    .permitAll()
                    .requestMatchers("/login/**")
                    .permitAll()
                    .requestMatchers("/test/**")
                    .permitAll()

                    // OAuth2 관련 엔드포인트
                    .requestMatchers("/oauth2/authorization/**")
                    .permitAll()
                    .requestMatchers("/login/oauth2/code/**")
                    .permitAll()

                    // WebSocket 관련 엔드포인트
                    .requestMatchers("/ws/**")
                    .permitAll()
                    .requestMatchers("/topic/**")
                    .permitAll()
                    .requestMatchers("/app/**")
                    .permitAll()

                    // Swagger 관련 엔드포인트
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()

                    // 정적 리소스
                    .requestMatchers("/static/**")
                    .permitAll()

                    // 나머지 모든 요청은 인증 필요
                    .anyRequest()
                    .authenticated())

        // OAuth2 로그인 설정
        .oauth2Login(
            oauth2 ->
                oauth2.successHandler(oauth2SuccessHandler).failureHandler(oauth2FailureHandler))

        // JWT 필터 추가
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /** CORS 설정 프론트엔드와의 통신을 위한 CORS 정책 설정 */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // 프론트엔드 주소들 허용 (IP와 도메인 모두 포함)
    configuration.setAllowedOrigins(
        List.of(
            "http://15.164.70.242",
            "http://43.200.225.0",
            "http://finland.r-e.kr",
            "http://localhost:3000",
            "http://localhost:5173"));
    // 와일드카드 패턴도 허용 (개발 환경용)
    configuration.setAllowedOriginPatterns(
        List.of(
            "http://15.164.70.242:*",
            "http://43.200.225.0:*",
            "http://finland.r-e.kr:*",
            "http://localhost:*"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "Set-Cookie"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
