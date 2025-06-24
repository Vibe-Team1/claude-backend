package com.example.claude_backend.domain.user.exception;

public class BackgroundAlreadyOwnedException extends RuntimeException {
  public BackgroundAlreadyOwnedException(String backgroundCode) {
    super("이미 보유 중인 배경입니다: " + backgroundCode);
  }
}
