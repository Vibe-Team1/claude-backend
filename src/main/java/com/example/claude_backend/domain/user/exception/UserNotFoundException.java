package com.example.claude_backend.domain.user.exception;

import java.util.UUID;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "사용자를 찾을 수 없습니다.";

    /**
     * 기본 메시지로 예외 생성
     */
    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * 사용자 ID로 예외 생성
     *
     * @param userId 찾을 수 없는 사용자 ID
     */
    public UserNotFoundException(UUID userId) {
        super(String.format("사용자를 찾을 수 없습니다. ID: %s", userId));
    }

    /**
     * 이메일로 예외 생성
     *
     * @param email 찾을 수 없는 사용자 이메일
     */
    public UserNotFoundException(String email) {
        super(String.format("사용자를 찾을 수 없습니다. 이메일: %s", email));
    }

    /**
     * 커스텀 메시지로 예외 생성
     *
     * @param message 예외 메시지
     */
    public static UserNotFoundException withMessage(String message) {
        return new UserNotFoundException() {
            @Override
            public String getMessage() {
                return message;
            }
        };
    }
}