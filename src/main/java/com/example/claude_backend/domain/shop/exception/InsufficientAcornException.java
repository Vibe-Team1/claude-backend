package com.example.claude_backend.domain.shop.exception;

public class InsufficientAcornException extends RuntimeException {
    public InsufficientAcornException(int required, int current) {
        super(String.format("도토리가 부족합니다. 필요: %d, 보유: %d", required, current));
    }
}