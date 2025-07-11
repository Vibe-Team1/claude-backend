package com.example.claude_backend.domain.oauth.repository;

import com.example.claude_backend.domain.oauth.entity.OAuthToken;
import com.example.claude_backend.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, UUID> {
  Optional<OAuthToken> findByUser(User user);

  /**
   * 사용자 ID로 토큰을 조회합니다.
   *
   * @param userId 사용자 ID
   * @return OAuth 토큰 (Optional)
   */
  Optional<OAuthToken> findByUserId(UUID userId);
}
