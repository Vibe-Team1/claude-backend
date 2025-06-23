package com.example.claude_backend.infrastructure.security.oauth2;

import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * OAuth2 로그인 실패 시 처리하는 핸들러
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

  /** OAuth2 로그인 실패 처리 */
  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    log.error("OAuth2 인증 실패: {}", exception.getMessage());

    // JSON 응답으로 변경
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ApiResponse<String> errorResponse =
        ApiResponse.error(
            "OAUTH2_AUTHENTICATION_FAILED", "OAuth2 인증에 실패했습니다: " + exception.getMessage());

    ObjectMapper mapper = new ObjectMapper();
    response.getWriter().write(mapper.writeValueAsString(errorResponse));
    response.getWriter().flush();
  }

  /** 쿠키 조회 */
  private java.util.Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (name.equals(cookie.getName())) {
          return java.util.Optional.of(cookie);
        }
      }
    }

    return java.util.Optional.empty();
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
