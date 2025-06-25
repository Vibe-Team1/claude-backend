package com.example.claude_backend.application.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 생성 요청 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsCreateRequest {

  /** 뉴스 요약 */
  @NotBlank(message = "뉴스 요약은 필수입니다.")
  @Size(max = 255, message = "뉴스 요약은 255자를 초과할 수 없습니다.")
  private String summary;
}
