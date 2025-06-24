package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "user_characters",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "character_code"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCharacter extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference
  private User user;

  @Column(name = "character_code", nullable = false, length = 3)
  private String characterCode;

  @Column(name = "acquired_at", nullable = false)
  private LocalDateTime acquiredAt;

  @Builder
  public UserCharacter(User user, String characterCode) {
    this.user = user;
    this.characterCode = characterCode;
    this.acquiredAt = LocalDateTime.now();
  }

  public void setUser(User user) {
    this.user = user;
  }
}
