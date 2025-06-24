package com.example.claude_backend.domain.shop.exception;

public class InvalidDrawTypeException extends RuntimeException {
  public InvalidDrawTypeException(String type) {
    super("유효하지 않은 뽑기 타입입니다: " + type);
  }
}
