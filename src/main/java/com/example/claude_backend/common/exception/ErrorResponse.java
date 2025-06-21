package com.example.claude_backend.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 에러 응답 DTO
 * GlobalExceptionHandler에서 사용하는 표준 에러 응답 형식
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * 에러 코드
     */
    private String code;

    /**
     * 에러 메시지
     */
    private String message;

    /**
     * 상세 에러 정보 (개발 환경에서만 포함)
     */
    private String detail;

    /**
     * 필드별 에러 정보 (Validation 에러의 경우)
     */
    private List<FieldError> fieldErrors;

    /**
     * 에러 발생 시간
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 요청 경로
     */
    private String path;

    /**
     * 단순 에러 응답 생성
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * 상세 정보를 포함한 에러 응답 생성
     */
    public static ErrorResponse of(String code, String message, String detail) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .detail(detail)
                .build();
    }

    /**
     * Validation 에러 응답 생성
     */
    public static ErrorResponse ofValidation(String message, List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .fieldErrors(fieldErrors)
                .build();
    }

    /**
     * 필드별 에러 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        /**
         * 필드명
         */
        private String field;

        /**
         * 입력값
         */
        private Object value;

        /**
         * 에러 메시지
         */
        private String reason;

        /**
         * 간단한 필드 에러 생성
         */
        public static FieldError of(String field, String reason) {
            return FieldError.builder()
                    .field(field)
                    .reason(reason)
                    .build();
        }

        /**
         * 값을 포함한 필드 에러 생성
         */
        public static FieldError of(String field, Object value, String reason) {
            return FieldError.builder()
                    .field(field)
                    .value(value)
                    .reason(reason)
                    .build();
        }
    }
}