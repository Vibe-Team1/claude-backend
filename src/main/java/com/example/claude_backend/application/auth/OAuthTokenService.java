package com.example.claude_backend.application.auth;

import com.example.claude_backend.domain.oauth.entity.OAuthToken;
import com.example.claude_backend.domain.oauth.repository.OAuthTokenRepository;
import com.example.claude_backend.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuthTokenService {

  private final OAuthTokenRepository oauthTokenRepository;

  public void saveToken(
      User user,
      String provider,
      String accessToken,
      String refreshToken,
      LocalDateTime expiresAt) {
    // 기존 토큰이 있는지 확인
    OAuthToken existingToken = oauthTokenRepository.findByUser(user).orElse(null);

    if (existingToken != null) {
      // 기존 토큰 업데이트
      existingToken = OAuthToken.builder()
          .id(existingToken.getId())
          .user(user)
          .provider(provider)
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .expiresAt(expiresAt)
          .createdAt(existingToken.getCreatedAt())
          .updatedAt(LocalDateTime.now())
          .build();
      log.info("기존 OAuth 토큰 업데이트 - 사용자 ID: {}", user.getId());
    } else {
      // 새 토큰 생성
      existingToken = OAuthToken.builder()
          .user(user)
          .provider(provider)
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .expiresAt(expiresAt)
          .build();
      log.info("새 OAuth 토큰 생성 - 사용자 ID: {}", user.getId());
    }

    oauthTokenRepository.save(existingToken);
    log.info("OAuth 토큰 저장 완료 - 사용자 ID: {}, 프로바이더: {}", user.getId(), provider);
  }

  public OAuthToken findByUser(User user) {
    return oauthTokenRepository.findByUser(user).orElse(null);
  }

  /**
   * 사용자 ID로 최신 토큰을 조회합니다.
   * 
   * @param userId 사용자 ID
   * @return 최신 OAuth 토큰 (Optional)
   */
  @Transactional(readOnly = true)
  public Optional<OAuthToken> findLatestTokenByUserId(UUID userId) {
    return oauthTokenRepository.findByUserId(userId);
  }
}
