package com.example.claude_backend.infrastructure.security.oauth2;

import com.example.claude_backend.application.auth.OAuthTokenService;
import com.example.claude_backend.domain.oauth.entity.OAuthToken;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.infrastructure.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * OAuth2 로그인 성공 시 처리하는 핸들러
 * 프론트엔드로 리디렉션하면서 토큰을 response body에 포함
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Value("${app.oauth2.authorized-redirect-uris}")
  private String[] authorizedRedirectUris;

  @Value("${app.frontend.oauth-success-url:http://localhost:3000/oauth-success}")
  private String frontendOAuthSuccessUrl;

  private final OAuthTokenService oauthTokenService;
  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    // 인증 정보를 SecurityContext에 명시적으로 설정
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // OAuth2UserPrincipal에서 사용자 정보 추출
    OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
    UUID userId = principal.getId();
    String userEmail = principal.getEmail();

    log.info("OAuth2 로그인 성공 - 사용자 ID: {}, 이메일: {}", userId, userEmail);

    // 최신 토큰 정보 조회
    Optional<OAuthToken> oauthTokenOpt = oauthTokenService.findLatestTokenByUserId(userId);

    if (oauthTokenOpt.isPresent()) {
      OAuthToken oauthToken = oauthTokenOpt.get();

      // accessToken만 쿼리 파라미터로 전달
      String targetUrl = frontendOAuthSuccessUrl + "?status=success&access_token=" + oauthToken.getAccessToken();

      log.debug("프론트엔드로 리디렉션: {}", targetUrl);

      // 리디렉션
      getRedirectStrategy().sendRedirect(request, response, targetUrl);
    } else {
      log.warn("사용자 {}의 토큰 정보를 찾을 수 없습니다.", userId);

      // 토큰이 없는 경우 에러 페이지로 리디렉션
      String errorUrl = frontendOAuthSuccessUrl + "?status=error&message=token_not_found";
      getRedirectStrategy().sendRedirect(request, response, errorUrl);
    }
  }

  /** 리다이렉트 URL 결정 - 토큰을 쿼리 파라미터로 포함 */
  protected String determineTargetUrl(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication, OAuthToken oauthToken) {

    // 프론트엔드 OAuth 성공 URL로 리디렉션
    String targetUrl = frontendOAuthSuccessUrl;

    // 토큰을 쿼리 파라미터로 추가
    targetUrl += "?status=success";
    targetUrl += "&access_token=" + oauthToken.getAccessToken();
    targetUrl += "&refresh_token=" + oauthToken.getRefreshToken();
    targetUrl += "&user_id=" + oauthToken.getUser().getId();

    return targetUrl;
  }

  /** 인증 관련 쿠키 삭제 (인증 속성은 유지) */
  protected void clearAuthenticationAttributes(
      HttpServletRequest request, HttpServletResponse response) {
    // super.clearAuthenticationAttributes(request); // 이 부분을 주석 처리하여 인증 속성 유지
    deleteCookie(request, response, "redirect_uri");
  }

  /** 승인된 리다이렉트 URI인지 확인 */
  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);

    for (String authorizedUri : authorizedRedirectUris) {
      URI authorizedURI = URI.create(authorizedUri);

      if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
          && authorizedURI.getPort() == clientRedirectUri.getPort()
          && authorizedURI.getPath().equals(clientRedirectUri.getPath())) {
        return true;
      }
    }

    return false;
  }

  /** 쿠키 조회 */
  private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (name.equals(cookie.getName())) {
          return Optional.of(cookie);
        }
      }
    }

    return Optional.empty();
  }

  /** 쿠키 삭제 */
  private void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
    Cookie cookie = new Cookie(name, "");
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }
}
