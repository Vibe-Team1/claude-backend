package com.example.claude_backend.domain.news.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class News extends BaseTimeEntity {

  /** 뉴스 ID */
  @Id @GeneratedValue private UUID id;

  /** 뉴스 요약 */
  @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
  private String summary;
}
