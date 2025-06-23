package com.example.claude_backend.domain.user.exception;

import java.util.UUID;

public class BackgroundNotOwnedException extends RuntimeException {
    public BackgroundNotOwnedException(UUID userId, String backgroundCode) {
        super("보유하지 않은 배경입니다: " + userId + " -> " + backgroundCode);
    }
}