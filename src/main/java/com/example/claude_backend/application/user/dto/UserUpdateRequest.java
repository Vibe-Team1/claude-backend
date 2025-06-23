package com.example.claude_backend.application.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 요청 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

  /** 닉네임 (수정 가능) - 2-20자 - 한글, 영문, 숫자, 언더스코어만 허용 */
  @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
  @Pattern(regexp = "^[가-힣a-zA-Z0-9_]+$", message = "닉네임은 한글, 영문, 숫자, 언더스코어만 사용 가능합니다.")
  private String nickname;

  /** 프로필 이미지 URL */
  @Size(max = 500, message = "프로필 이미지 URL은 500자를 초과할 수 없습니다.")
  @Pattern(regexp = "^(https?://.*\\.(jpg|jpeg|png|gif|webp))?$", message = "올바른 이미지 URL 형식이 아닙니다.")
  private String profileImageUrl;

  /** 자기소개 */
  @Size(max = 1000, message = "자기소개는 1000자를 초과할 수 없습니다.")
  private String bio;
}
