package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.auth.OAuthTokenService;
import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.domain.oauth.entity.OAuthToken;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 성공 후 토큰을 표시하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthSuccessController {

    private final UserService userService;
    private final OAuthTokenService oauthTokenService;

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
}