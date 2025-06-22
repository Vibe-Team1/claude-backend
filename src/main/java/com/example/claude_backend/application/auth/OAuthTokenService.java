package com.example.claude_backend.application.auth;

import com.example.claude_backend.domain.oauth.entity.OAuthToken;
import com.example.claude_backend.domain.oauth.repository.OAuthTokenRepository;
import com.example.claude_backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OAuthTokenService {

    private final OAuthTokenRepository oauthTokenRepository;

    public void saveToken(User user, String provider, String accessToken, String refreshToken, LocalDateTime expiresAt) {
        oauthTokenRepository.save(
                OAuthToken.builder()
                        .user(user)
                        .provider(provider)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresAt(expiresAt)
                        .build()
        );
    }
}
