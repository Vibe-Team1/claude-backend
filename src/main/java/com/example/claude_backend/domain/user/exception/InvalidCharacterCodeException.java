package com.example.claude_backend.domain.user.exception;

public class InvalidCharacterCodeException extends RuntimeException {
    public InvalidCharacterCodeException(String characterCode) {
        super("유효하지 않은 캐릭터 코드입니다: " + characterCode);
    }
}