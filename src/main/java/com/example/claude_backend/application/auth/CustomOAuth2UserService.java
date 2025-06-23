package com.example.claude_backend.application.auth;

import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.infrastructure.security.jwt.JwtTokenProvider;
import com.example.claude_backend.infrastructure.security.oauth2.OAuth2UserPrincipal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** OAuth2 로그인 시 사용자 정보를 처리하는 서비스 Google OAuth2 로그인 후 사용자 정보를 DB에 저장/업데이트하고 JWT 토큰을 저장 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserService userService;
  private final OAuthTokenService oauthTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.debug("OAuth2 사용자 정보 로드 시작");

    OAuth2User oauth2User = super.loadUser(userRequest);

    try {
      return processOAuth2User(userRequest, oauth2User);
    } catch (Exception ex) {
      log.error("OAuth2 사용자 처리 중 오류 발생", ex);
      throw new OAuth2AuthenticationException("OAuth2 사용자 처리 실패: " + ex.getMessage());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    if (!"google".equalsIgnoreCase(registrationId)) {
      throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 공급자입니다: " + registrationId);
    }

    Map<String, Object> attributes = oauth2User.getAttributes();
    String googleSub = (String) attributes.get("sub");
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String picture = (String) attributes.get("picture");

    log.info("Google OAuth2 로그인 - 이메일: {}, 이름: {}", email, name);

    Boolean emailVerified = (Boolean) attributes.get("email_verified");
    if (emailVerified == null || !emailVerified) {
      throw new OAuth2AuthenticationException("이메일 인증이 필요합니다.");
    }

    // 사용자 생성 또는 업데이트
    User user = userService.processOAuthLogin(email, googleSub, name, picture);

    // JWT Access / Refresh 토큰 생성
    String accessToken = jwtTokenProvider.createToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

    // Access Token 만료 시간 계산
    Date accessTokenExpiry = jwtTokenProvider.getTokenExpiry(accessToken);
    LocalDateTime expiresAt =
        accessTokenExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    // DB에 oauth_tokens 저장
    oauthTokenService.saveToken(
        user, registrationId.toUpperCase(), accessToken, refreshToken, expiresAt);

    // 최종 OAuth2UserPrincipal 반환
    return OAuth2UserPrincipal.create(user, attributes);
  }
}
