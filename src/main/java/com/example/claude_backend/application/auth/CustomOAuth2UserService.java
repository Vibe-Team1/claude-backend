package com.example.claude_backend.application.auth;

import java.util.Map;

import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.infrastructure.security.oauth2.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 로그인 시 사용자 정보를 처리하는 서비스
 * Google OAuth2 로그인 후 사용자 정보를 DB에 저장/업데이트
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    /**
     * OAuth2 로그인 후 사용자 정보 처리
     *
     * @param userRequest OAuth2 사용자 요청
     * @return OAuth2User 구현체
     * @throws OAuth2AuthenticationException 인증 실패 시
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("OAuth2 사용자 정보 로드 시작");

        // OAuth2 공급자로부터 사용자 정보 가져오기
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("OAuth2 사용자 처리 중 오류 발생", ex);
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 실패: " + ex.getMessage());
        }
    }

    /**
     * OAuth2 사용자 정보 처리
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // OAuth2 공급자 확인 (현재는 Google만 지원)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"google".equalsIgnoreCase(registrationId)) {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 공급자입니다: " + registrationId);
        }

        // Google 사용자 정보 추출
        Map<String, Object> attributes = oauth2User.getAttributes();
        String googleSub = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        log.info("Google OAuth2 로그인 - 이메일: {}, 이름: {}", email, name);

        // 이메일 검증
        Boolean emailVerified = (Boolean) attributes.get("email_verified");
        if (emailVerified == null || !emailVerified) {
            throw new OAuth2AuthenticationException("이메일 인증이 필요합니다.");
        }

        // 사용자 생성 또는 업데이트
        User user = userService.processOAuthLogin(email, googleSub, name, picture);

        // OAuth2UserPrincipal 생성 및 반환
        return OAuth2UserPrincipal.create(user, attributes);
    }
}