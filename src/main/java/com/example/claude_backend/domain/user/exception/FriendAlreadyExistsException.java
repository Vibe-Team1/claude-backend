package com.example.claude_backend.domain.user.exception;

import java.util.UUID;

public class FriendAlreadyExistsException extends RuntimeException {
  public FriendAlreadyExistsException(UUID userId, UUID friendId) {
    super("이미 친구 관계가 존재합니다: " + userId + " -> " + friendId);
  }
}
