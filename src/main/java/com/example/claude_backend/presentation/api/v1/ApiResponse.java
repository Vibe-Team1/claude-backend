package com.example.claude_backend.presentation.api.v1;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 응답 표준 래퍼 클래스
 *
 * @param <T> 응답 데이터 타입
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * 성공 여부
     */
    private boolean success;

    /**
     * 응답 데이터
     */
    private T data;

    /**
     * 에러 정보
     */
    private ErrorInfo error;

    /**
     * 응답 시간
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 성공 응답 생성
     *
     * @param data 응답 데이터
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * 실패 응답 생성
     *
     * @param code 에러 코드
     * @param message 에러 메시지
     * @return 실패 응답
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

    /**
     * 실패 응답 생성 (필드 에러 포함)
     *
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param field 에러 필드
     * @return 실패 응답
     */
    public static <T> ApiResponse<T> error(String code, String message, String field) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .field(field)
                        .build())
                .build();
    }

    /**
     * 에러 정보 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorInfo {
        /**
         * 에러 코드
         */
        private String code;

        /**
         * 에러 메시지
         */
        private String message;

        /**
         * 에러 필드 (optional)
         */
        private String field;
    }
}