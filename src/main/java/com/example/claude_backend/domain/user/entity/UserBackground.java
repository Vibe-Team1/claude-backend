package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "user_backgrounds",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "background_code"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBackground extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "background_code", nullable = false, length = 2)
  private String backgroundCode;

  @Column(name = "acquired_at", nullable = false)
  private LocalDateTime acquiredAt;

  @Builder
  public UserBackground(UUID userId, String backgroundCode) {
    this.userId = userId;
    this.backgroundCode = backgroundCode;
    this.acquiredAt = LocalDateTime.now();
  }
}
