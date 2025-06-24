package com.example.claude_backend.domain.user.exception;

import java.util.UUID;

public class CharacterNotOwnedException extends RuntimeException {
  public CharacterNotOwnedException(UUID userId, String characterCode) {
    super("보유하지 않은 캐릭터입니다: " + userId + " -> " + characterCode);
  }
}
