package com.example.claude_backend.domain.oauth.repository;

import com.example.claude_backend.domain.oauth.entity.OAuthToken;
import com.example.claude_backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, UUID> {
    Optional<OAuthToken> findByUser(User user);
}
