package com.example.claude_backend.domain.user.exception;

public class CannotAddSelfAsFriendException extends RuntimeException {
  public CannotAddSelfAsFriendException() {
    super("자신을 친구로 추가할 수 없습니다.");
  }
}
