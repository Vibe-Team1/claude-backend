package com.example.claude_backend.domain.user.exception;

public class CharacterAlreadyOwnedException extends RuntimeException {
    public CharacterAlreadyOwnedException(String characterCode) {
        super("이미 보유 중인 캐릭터입니다: " + characterCode);
    }
}