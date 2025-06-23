package com.example.claude_backend.application.user.dto;

import com.example.claude_backend.domain.user.entity.UserProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 프로필 응답 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

  /** 프로필 이미지 URL */
  private String profileImageUrl;

  /** 자기소개 */
  private String bio;

  /** 총 자산 (모의투자) */
  private Long totalAssets;

  /** 룸 레벨 */
  private Integer roomLevel;

  /**
   * Entity를 DTO로 변환하는 정적 팩토리 메서드
   *
   * @param profile UserProfile 엔티티
   * @return UserProfileResponse DTO
   */
  public static UserProfileResponse from(UserProfile profile) {
    return UserProfileResponse.builder()
        .profileImageUrl(profile.getProfileImageUrl())
        .bio(profile.getBio())
        .totalAssets(profile.getTotalAssets())
        .roomLevel(profile.getRoomLevel())
        .build();
  }
}
