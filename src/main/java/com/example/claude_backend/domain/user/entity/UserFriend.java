package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "user_friends",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "friend_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFriend extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "friend_id", nullable = false)
  private UUID friendId;

  @Builder
  public UserFriend(UUID userId, UUID friendId) {
    this.userId = userId;
    this.friendId = friendId;
  }
}
