package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.infrastructure.security.jwt.JwtTokenProvider;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 개발/테스트용 인증 컨트롤러
 * OAuth2 없이 직접 토큰을 발급받을 수 있음
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test/auth")
@RequiredArgsConstructor
@Profile({"local", "test"})  // 개발 환경에서만 활성화
public class TestAuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 테스트용 로그인 (이메일로 직접 로그인)
     *
     * @param email 사용자 이메일
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> testLogin(
            @RequestParam("email") String email) {
        log.info("테스트 로그인 요청: {}", email);

        try {
            // 이메일로 사용자 조회 또는 생성
            User user = userService.processOAuthLogin(
                    email,
                    "test_google_sub_" + email.hashCode(),
                    email.split("@")[0],
                    null
            );

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createToken(user.getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            tokens.put("userId", user.getId().toString());
            tokens.put("email", user.getEmail());

            return ResponseEntity.ok(ApiResponse.success(tokens));
        } catch (Exception e) {
            log.error("테스트 로그인 실패", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("LOGIN_FAILED", e.getMessage()));
        }
    }

    /**
     * 토큰 검증
     *
     * @param token JWT 토큰
     * @return 토큰 정보
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyToken(
            @RequestParam("token") String token) {

        if (jwtTokenProvider.validateToken(token)) {
            Map<String, Object> info = new HashMap<>();
            info.put("valid", true);
            info.put("userId", jwtTokenProvider.getUserIdFromToken(token));
            info.put("expiry", jwtTokenProvider.getTokenExpiry(token));

            return ResponseEntity.ok(ApiResponse.success(info));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_TOKEN", "유효하지 않은 토큰입니다."));
        }
    }
}