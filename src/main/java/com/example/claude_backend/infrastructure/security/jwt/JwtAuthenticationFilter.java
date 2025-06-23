package com.example.claude_backend.infrastructure.security.jwt;

import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.infrastructure.security.oauth2.OAuth2UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 토큰을 검증하고 인증 정보를 설정하는 필터
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final UserService userService;

  /** 요청마다 JWT 토큰 검증 및 인증 처리 */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        UUID userId = tokenProvider.getUserIdFromToken(jwt);

        // DB 조회 없이 간단한 Principal 생성
        OAuth2UserPrincipal principal =
            new OAuth2UserPrincipal(
                userId,
                "user@example.com", // 임시 이메일
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                new HashMap<>());

        // Spring Security 인증 정보 설정
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JWT 인증 성공. 사용자 ID: {}", userId);
      }
    } catch (Exception ex) {
      log.error("Spring Security에 사용자 인증을 설정할 수 없습니다.", ex);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Request에서 JWT 토큰 추출
   *
   * @param request HTTP 요청
   * @return JWT 토큰 또는 null
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}
