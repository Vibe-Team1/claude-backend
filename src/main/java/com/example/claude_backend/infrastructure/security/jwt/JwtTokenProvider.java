package com.example.claude_backend.infrastructure.security.jwt;

import com.example.claude_backend.infrastructure.security.oauth2.OAuth2UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * JWT 토큰 생성 및 검증을 담당하는 컴포넌트
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long tokenExpiry;
    private final long refreshTokenExpiry;

    public JwtTokenProvider(
            @Value("${app.auth.token-secret}") String tokenSecret,
            @Value("${app.auth.token-expiry}") long tokenExpiry,
            @Value("${app.auth.refresh-token-expiry}") long refreshTokenExpiry
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenExpiry = tokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    /**
     * 인증 정보로부터 JWT 토큰 생성
     *
     * @param authentication 인증 정보
     * @return JWT 토큰
     */
    public String createToken(Authentication authentication) {
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        return createToken(principal.getId());
    }

    /**
     * 사용자 ID로 JWT 토큰 생성
     *
     * @param userId 사용자 ID
     * @return JWT 토큰
     */
    public String createToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpiry);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     *
     * @param userId 사용자 ID
     * @return 리프레시 토큰
     */
    public String createRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiry);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)  // setSigningKey 대신 verifyWith 사용
                .build()
                .parseSignedClaims(token)  // parseClaimsJws 대신 parseSignedClaims
                .getPayload();  // getBody() 대신 getPayload()

        return UUID.fromString(claims.getSubject());
    }
    /**
     * JWT 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException ex) {
            log.error("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException ex) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            log.error("JWT 토큰이 비어있습니다.");
        }
        return false;
    }

    /**
     * 토큰이 리프레시 토큰인지 확인
     *
     * @param token JWT 토큰
     * @return 리프레시 토큰 여부
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰 만료 시간 조회
     *
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date getTokenExpiry(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }
}