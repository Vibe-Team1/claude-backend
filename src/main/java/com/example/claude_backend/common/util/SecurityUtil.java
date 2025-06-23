package com.example.claude_backend.common.util;

import com.example.claude_backend.infrastructure.security.oauth2.OAuth2UserPrincipal;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 관련 유틸리티 클래스
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public class SecurityUtil {

  private SecurityUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 현재 인증된 사용자 ID 조회
   *
   * @return 사용자 ID
   * @throws IllegalStateException 인증되지 않은 경우
   */
  public static UUID getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }

    if (authentication.getPrincipal() instanceof OAuth2UserPrincipal) {
      OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
      return principal.getId();
    }

    throw new IllegalStateException("잘못된 인증 정보입니다.");
  }

  /**
   * 현재 인증된 사용자 ID 조회 (Long 타입)
   *
   * @return 사용자 ID (Long)
   * @throws IllegalStateException 인증되지 않은 경우
   */
  public static Long getCurrentUserId(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }

    if (authentication.getPrincipal() instanceof OAuth2UserPrincipal) {
      OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
      // UUID를 Long으로 변환 (간단한 해시 기반)
      return (long) Math.abs(principal.getId().hashCode());
    }

    throw new IllegalStateException("잘못된 인증 정보입니다.");
  }

  /**
   * 현재 인증된 사용자 ID 조회 (없으면 null)
   *
   * @return 사용자 ID 또는 null
   */
  public static UUID getCurrentUserIdOrNull() {
    try {
      return getCurrentUserId();
    } catch (IllegalStateException e) {
      return null;
    }
  }

  /**
   * 현재 사용자 이메일 조회
   *
   * @return 사용자 이메일
   */
  public static String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }

    if (authentication.getPrincipal() instanceof OAuth2UserPrincipal) {
      OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
      return principal.getEmail();
    }

    throw new IllegalStateException("잘못된 인증 정보입니다.");
  }

  /**
   * 현재 사용자 인증 여부 확인
   *
   * @return 인증 여부
   */
  public static boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.isAuthenticated()
        && authentication.getPrincipal() instanceof OAuth2UserPrincipal;
  }

  /**
   * 현재 사용자가 특정 역할을 가지고 있는지 확인
   *
   * @param role 역할명 (ROLE_ 접두사 포함)
   * @return 역할 보유 여부
   */
  public static boolean hasRole(String role) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    return authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals(role));
  }

  /**
   * 현재 사용자가 관리자인지 확인
   *
   * @return 관리자 여부
   */
  public static boolean isAdmin() {
    return hasRole("ROLE_ADMIN");
  }
}
