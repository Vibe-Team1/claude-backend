package com.example.claude_backend.domain.user.exception;

public class InvalidBackgroundCodeException extends RuntimeException {
  public InvalidBackgroundCodeException(String backgroundCode) {
    super("유효하지 않은 배경 코드입니다: " + backgroundCode);
  }
}
