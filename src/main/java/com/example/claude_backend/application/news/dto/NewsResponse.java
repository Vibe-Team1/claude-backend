package com.example.claude_backend.application.news.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 응답 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {

  /** 뉴스 ID */
  private UUID id;

  /** 뉴스 요약 */
  private String summary;

  /** 생성 일시 */
  private LocalDateTime createdAt;

  /** 수정 일시 */
  private LocalDateTime updatedAt;
}
