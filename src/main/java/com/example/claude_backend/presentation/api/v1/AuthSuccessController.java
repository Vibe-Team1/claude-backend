package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.auth.OAuthTokenService;
import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.domain.oauth.entity.OAuthToken;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.infrastructure.security.jwt.JwtTokenProvider;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/** OAuth2 로그인 성공 후 토큰을 표시하는 컨트롤러 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthSuccessController {

  private final UserService userService;
  private final OAuthTokenService oauthTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("/success")
  public ResponseEntity<ApiResponse<Map<String, Object>>> authSuccess() {
    try {
      // 현재 인증 정보 확인
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      log.info("현재 인증 정보: {}", authentication);

      if (authentication == null || !authentication.isAuthenticated()) {
        log.warn("인증되지 않은 사용자입니다.");
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("UNAUTHORIZED", "인증되지 않은 사용자입니다."));
      }

      // 현재 인증된 사용자 정보 가져오기
      String userEmail = SecurityUtil.getCurrentUserEmail();
      log.info("현재 사용자 이메일: {}", userEmail);

      User user = userService.findByEmail(userEmail);
      log.info("사용자 정보 조회 완료: {}", user);

      // OAuth 토큰 정보 가져오기
      OAuthToken oauthToken = oauthTokenService.findByUser(user);

      Map<String, Object> responseData = new HashMap<>();
      responseData.put("message", "OAuth 로그인 성공");

      // User 엔티티 대신 필요한 정보만 포함
      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("id", user.getId());
      userInfo.put("email", user.getEmail());
      userInfo.put("nickname", user.getNickname());
      userInfo.put("status", user.getStatus());
      userInfo.put("createdAt", user.getCreatedAt());
      userInfo.put("updatedAt", user.getUpdatedAt());
      responseData.put("user", userInfo);

      if (oauthToken != null) {
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("provider", oauthToken.getProvider());
        tokenInfo.put("expiresAt", oauthToken.getExpiresAt());
        tokenInfo.put("createdAt", oauthToken.getCreatedAt());
        tokenInfo.put("updatedAt", oauthToken.getUpdatedAt());
        responseData.put("tokenInfo", tokenInfo);
        log.info("OAuth 토큰 조회 성공 - 사용자 ID: {}, 프로바이더: {}", user.getId(), oauthToken.getProvider());
      } else {
        log.warn("OAuth 토큰을 찾을 수 없음 - 사용자 ID: {}", user.getId());
      }

      return ResponseEntity.ok(ApiResponse.success(responseData));

    } catch (IllegalStateException e) {
      log.error("인증 정보 오류: {}", e.getMessage());
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("AUTH_ERROR", "인증 정보 오류: " + e.getMessage()));
    } catch (Exception e) {
      log.error("OAuth 로그인 성공 처리 중 오류 발생", e);
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("OAUTH_ERROR", "OAuth 로그인 처리 중 오류가 발생했습니다: " + e.getMessage()));
    }
  }

  @GetMapping("/token")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getTokenInfo() {
    try {
      // 현재 인증 정보 확인
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      log.info("토큰 조회 - 현재 인증 정보: {}", authentication);

      if (authentication == null || !authentication.isAuthenticated()) {
        log.warn("토큰 조회 - 인증되지 않은 사용자입니다.");
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("UNAUTHORIZED", "인증되지 않은 사용자입니다."));
      }

      String userEmail = SecurityUtil.getCurrentUserEmail();
      User user = userService.findByEmail(userEmail);
      OAuthToken oauthToken = oauthTokenService.findByUser(user);

      if (oauthToken == null) {
        return ResponseEntity.notFound().build();
      }

      Map<String, Object> tokenInfo = new HashMap<>();
      tokenInfo.put("provider", oauthToken.getProvider());
      tokenInfo.put("expiresAt", oauthToken.getExpiresAt());
      tokenInfo.put("createdAt", oauthToken.getCreatedAt());
      tokenInfo.put("updatedAt", oauthToken.getUpdatedAt());
      tokenInfo.put("hasAccessToken", oauthToken.getAccessToken() != null);
      tokenInfo.put("hasRefreshToken", oauthToken.getRefreshToken() != null);

      return ResponseEntity.ok(ApiResponse.success(tokenInfo));

    } catch (IllegalStateException e) {
      log.error("토큰 조회 - 인증 정보 오류: {}", e.getMessage());
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("AUTH_ERROR", "인증 정보 오류: " + e.getMessage()));
    } catch (Exception e) {
      log.error("토큰 정보 조회 중 오류 발생", e);
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("TOKEN_ERROR", "토큰 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
    }
  }

  /**
   * Refresh Token을 사용하여 새로운 Access Token 발급
   * 
   * @param refreshToken 리프레시 토큰
   * @return 새로운 액세스 토큰과 리프레시 토큰
   */
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(
      @RequestParam String refreshToken) {

    log.info("토큰 갱신 요청 - Refresh Token: {}", refreshToken.substring(0, Math.min(20, refreshToken.length())) + "...");

    try {
      // Refresh Token으로 사용자 ID 추출
      UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

      // 최신 토큰 정보 조회
      Optional<OAuthToken> oauthTokenOpt = oauthTokenService.findLatestTokenByUserId(userId);

      if (oauthTokenOpt.isPresent()) {
        OAuthToken oauthToken = oauthTokenOpt.get();

        // 저장된 Refresh Token과 일치하는지 확인
        if (!refreshToken.equals(oauthToken.getRefreshToken())) {
          log.warn("Refresh Token 불일치 - 사용자 ID: {}", userId);
          return ResponseEntity.badRequest()
              .body(ApiResponse.error("INVALID_REFRESH_TOKEN", "Invalid refresh token"));
        }

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // Access Token 만료 시간 계산
        Date accessTokenExpiry = jwtTokenProvider.getTokenExpiry(newAccessToken);
        LocalDateTime expiresAt = accessTokenExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // 토큰 정보 업데이트
        oauthTokenService.saveToken(oauthToken.getUser(), "GOOGLE", newAccessToken, newRefreshToken, expiresAt);

        // Response Body에 토큰 포함
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", newAccessToken);
        tokenResponse.put("refresh_token", newRefreshToken);
        tokenResponse.put("user_id", userId.toString());

        log.info("토큰 갱신 성공 - 사용자 ID: {}", userId);

        return ResponseEntity.ok(ApiResponse.success(tokenResponse));

      } else {
        log.warn("사용자 {}의 토큰 정보를 찾을 수 없습니다.", userId);
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("TOKEN_NOT_FOUND", "Token not found"));
      }

    } catch (Exception e) {
      log.error("토큰 갱신 중 오류 발생", e);
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("REFRESH_ERROR", "Token refresh failed"));
    }
  }

  /**
   * 현재 토큰 상태 확인
   * 
   * @param accessToken 액세스 토큰
   * @return 토큰 유효성 및 사용자 정보
   */
  @PostMapping("/verify")
  public ResponseEntity<ApiResponse<Map<String, Object>>> verifyToken(
      @RequestParam String accessToken) {

    log.info("토큰 검증 요청");

    try {
      // 토큰 유효성 검증
      if (!jwtTokenProvider.validateToken(accessToken)) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("INVALID_TOKEN", "Invalid token"));
      }

      // 사용자 ID 추출
      UUID userId = jwtTokenProvider.getUserIdFromToken(accessToken);

      // 토큰 정보 조회
      Optional<OAuthToken> oauthTokenOpt = oauthTokenService.findLatestTokenByUserId(userId);

      if (oauthTokenOpt.isPresent()) {
        OAuthToken oauthToken = oauthTokenOpt.get();

        Map<String, Object> verificationResponse = new HashMap<>();
        verificationResponse.put("valid", true);
        verificationResponse.put("user_id", userId.toString());
        verificationResponse.put("user_email", oauthToken.getUser().getEmail());

        return ResponseEntity.ok(ApiResponse.success(verificationResponse));

      } else {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("TOKEN_NOT_FOUND", "Token not found"));
      }

    } catch (Exception e) {
      log.error("토큰 검증 중 오류 발생", e);
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("VERIFICATION_ERROR", "Token verification failed"));
    }
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

  /** 토큰을 쿠키에 설정 */
  private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
    // Access Token 쿠키 설정
    Cookie accessTokenCookie = new Cookie("access_token", accessToken);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(false); // 개발환경에서는 false, 프로덕션에서는 true
    accessTokenCookie.setMaxAge(7 * 24 * 3600); // 7일

    response.addCookie(accessTokenCookie);

    // Refresh Token 쿠키 설정
    Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false); // 개발환경에서는 false, 프로덕션에서는 true
    refreshTokenCookie.setMaxAge(21 * 24 * 3600); // 21일

    response.addCookie(refreshTokenCookie);
  }
}
