package com.example.claude_backend.infrastructure.websocket.handler;

import com.example.claude_backend.infrastructure.security.jwt.JwtTokenProvider;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocket 핸드셰이크 인터셉터 JWT 토큰을 쿠키에서 추출하여 인증 처리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  /** 핸드셰이크 전 처리 JWT 토큰 검증 및 사용자 정보 설정 */
  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes)
      throws Exception {

    if (request instanceof ServletServerHttpRequest) {
      ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

      // 쿠키에서 JWT 토큰 추출
      String token = extractTokenFromCookies(servletRequest);

      if (token != null && jwtTokenProvider.validateToken(token)) {
        // 토큰에서 사용자 ID 추출
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);

        // WebSocket 세션에 사용자 정보 저장
        attributes.put("userId", userId);
        attributes.put("authenticated", true);

        log.debug("WebSocket 핸드셰이크 성공 - 사용자 ID: {}", userId);
        return true;
      } else {
        log.warn("WebSocket 핸드셰이크 실패 - 유효하지 않은 토큰");
        return false;
      }
    }

    log.warn("WebSocket 핸드셰이크 실패 - ServletServerHttpRequest가 아님");
    return false;
  }

  /** 핸드셰이크 후 처리 */
  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception) {
    if (exception != null) {
      log.error("WebSocket 핸드셰이크 중 오류 발생", exception);
    } else {
      log.debug("WebSocket 핸드셰이크 완료");
    }
  }

  /** 쿠키에서 JWT 토큰 추출 */
  private String extractTokenFromCookies(ServletServerHttpRequest request) {
    jakarta.servlet.http.Cookie[] cookies = request.getServletRequest().getCookies();

    if (cookies != null) {
      for (jakarta.servlet.http.Cookie cookie : cookies) {
        if ("accessToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    return null;
  }
}
