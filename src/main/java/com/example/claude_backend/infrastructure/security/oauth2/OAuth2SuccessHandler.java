package com.example.claude_backend.infrastructure.security.oauth2;

import com.example.claude_backend.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth2 로그인 성공 시 처리하는 핸들러
 * CustomOAuth2UserService에서 생성된 토큰을 사용하여 프론트엔드로 리다이렉트
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // 인증 정보를 SecurityContext에 명시적으로 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("응답이 이미 커밋되었습니다. {}로 리다이렉트할 수 없습니다.", targetUrl);
            return;
        }

        // 인증 속성은 유지 (clearAuthenticationAttributes 호출하지 않음)
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 리다이렉트 URL 결정
     * CustomOAuth2UserService에서 이미 토큰이 생성되고 저장되었으므로
     * 여기서는 단순히 리다이렉트 URL만 결정
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Optional<String> redirectUri = getCookie(request, "redirect_uri")
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("승인되지 않은 리다이렉트 URI입니다: " + redirectUri.get());
        }

        // Frontend 연결시 아래 해제
        // String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String targetUrl = "/auth/success"; // 프론트엔드 URL 대신 백엔드 URL 사용

        // OAuth2UserPrincipal에서 사용자 정보 로그
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        log.info("OAuth2 로그인 성공 - 사용자 ID: {}, 이메일: {}", principal.getId(), principal.getEmail());

        // 토큰은 CustomOAuth2UserService에서 이미 생성되고 저장되었으므로
        // 여기서는 단순히 성공 페이지로 리다이렉트
        return targetUrl;
    }

    /**
     * 인증 관련 쿠키 삭제 (인증 속성은 유지)
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        // super.clearAuthenticationAttributes(request); // 이 부분을 주석 처리하여 인증 속성 유지
        deleteCookie(request, response, "redirect_uri");
    }

    /**
     * 승인된 리다이렉트 URI인지 확인
     */
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

    /**
     * 쿠키 조회
     */
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

    /**
     * 쿠키 삭제
     */
    private void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}