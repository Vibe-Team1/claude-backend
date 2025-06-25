package com.example.claude_backend.infrastructure.security.jwt;

import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.infrastructure.security.oauth2.OAuth2UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 토큰을 통한 인증을 처리하는 필터 Authorization 헤더에서 Bearer 토큰을 추출하여 인증 처리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // Authorization 헤더에서 토큰 추출
      String token = getTokenFromRequest(request);

      if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
        // 토큰에서 사용자 ID 추출
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);

        // 사용자 정보 로드 (트랜잭션 내에서 처리)
        User user = userService.getUserEntityWithRolesById(userId);

        if (user != null) {
          // OAuth2UserPrincipal 생성 (roles 컬렉션이 이미 로드된 상태)
          OAuth2UserPrincipal principal = OAuth2UserPrincipal.create(user);

          // 인증 객체 생성
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

          // SecurityContext에 인증 정보 설정
          SecurityContextHolder.getContext().setAuthentication(authentication);

          log.debug("JWT 인증 성공 - 사용자 ID: {}", userId);
        }
      }
    } catch (Exception e) {
      log.error("JWT 인증 처리 중 오류 발생", e);
      // 인증 실패 시에도 요청은 계속 진행 (인증이 필요한 엔드포인트에서 처리)
    }

    filterChain.doFilter(request, response);
  }

  /**
   * HTTP 요청에서 JWT 토큰을 추출 Authorization 헤더의 Bearer 토큰 형식 지원
   *
   * @param request HTTP 요청
   * @return JWT 토큰 (없으면 null)
   */
  private String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}
