package com.example.claude_backend.application.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 검색 응답 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSearchResponse {

  /** 사용자 ID */
  private String userId;

  /** 닉네임 */
  private String nickname;

  /** 현재 선택된 캐릭터 코드 */
  private String currentCharacterCode;

  /** 계좌 잔액 */
  private Long balance;

  /** 보유 캐릭터 개수 */
  private Integer characterCount;
}
