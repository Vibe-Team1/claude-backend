package com.example.claude_backend.application.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 커스터마이제이션 응답 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCustomizationResponse {

  /** 보유 배경 코드 목록 */
  private List<String> backgrounds;

  /** 보유 캐릭터 코드 목록 */
  private List<String> characters;

  /** 보유 배경 URL 목록 */
  private List<String> backgroundUrls;

  /** 보유 캐릭터 URL 목록 */
  private List<String> characterUrls;
}
