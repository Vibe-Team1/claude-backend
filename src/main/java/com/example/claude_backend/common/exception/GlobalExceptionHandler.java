package com.example.claude_backend.common.exception;

import com.example.claude_backend.domain.user.exception.BackgroundAlreadyOwnedException;
import com.example.claude_backend.domain.user.exception.CannotAddSelfAsFriendException;
import com.example.claude_backend.domain.user.exception.CharacterAlreadyOwnedException;
import com.example.claude_backend.domain.user.exception.FriendAlreadyExistsException;
import com.example.claude_backend.domain.user.exception.InvalidBackgroundCodeException;
import com.example.claude_backend.domain.user.exception.InvalidCharacterCodeException;
import com.example.claude_backend.domain.user.exception.UserNotFoundException;
import com.example.claude_backend.domain.shop.exception.InsufficientAcornException;
import com.example.claude_backend.domain.shop.exception.InvalidDrawTypeException;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 전역 예외 처리기 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** UserNotFoundException 처리 */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(
      UserNotFoundException ex, WebRequest request) {
    log.error("사용자를 찾을 수 없습니다: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("USER_NOT_FOUND", ex.getMessage()));
  }

  /** BackgroundAlreadyOwnedException 처리 */
  @ExceptionHandler(BackgroundAlreadyOwnedException.class)
  public ResponseEntity<ApiResponse<Void>> handleBackgroundAlreadyOwnedException(
      BackgroundAlreadyOwnedException ex, WebRequest request) {
    log.error("이미 보유 중인 배경: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error("BACKGROUND_ALREADY_OWNED", ex.getMessage()));
  }

  /** CharacterAlreadyOwnedException 처리 */
  @ExceptionHandler(CharacterAlreadyOwnedException.class)
  public ResponseEntity<ApiResponse<Void>> handleCharacterAlreadyOwnedException(
      CharacterAlreadyOwnedException ex, WebRequest request) {
    log.error("이미 보유 중인 캐릭터: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error("CHARACTER_ALREADY_OWNED", ex.getMessage()));
  }

  /** InvalidBackgroundCodeException 처리 */
  @ExceptionHandler(InvalidBackgroundCodeException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidBackgroundCodeException(
      InvalidBackgroundCodeException ex, WebRequest request) {
    log.error("유효하지 않은 배경 코드: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("INVALID_BACKGROUND_CODE", ex.getMessage()));
  }

  /** InvalidCharacterCodeException 처리 */
  @ExceptionHandler(InvalidCharacterCodeException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidCharacterCodeException(
      InvalidCharacterCodeException ex, WebRequest request) {
    log.error("유효하지 않은 캐릭터 코드: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("INVALID_CHARACTER_CODE", ex.getMessage()));
  }

  /** FriendAlreadyExistsException 처리 */
  @ExceptionHandler(FriendAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<Void>> handleFriendAlreadyExistsException(
      FriendAlreadyExistsException ex, WebRequest request) {
    log.error("이미 친구 관계가 존재합니다: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error("FRIEND_ALREADY_EXISTS", ex.getMessage()));
  }

  /** CannotAddSelfAsFriendException 처리 */
  @ExceptionHandler(CannotAddSelfAsFriendException.class)
  public ResponseEntity<ApiResponse<Void>> handleCannotAddSelfAsFriendException(
      CannotAddSelfAsFriendException ex, WebRequest request) {
    log.error("자신을 친구로 추가할 수 없습니다: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("CANNOT_ADD_SELF_AS_FRIEND", ex.getMessage()));
  }

  /** InsufficientAcornException 처리 */
  @ExceptionHandler(InsufficientAcornException.class)
  public ResponseEntity<ApiResponse<Void>> handleInsufficientAcornException(
      InsufficientAcornException ex, WebRequest request) {
    log.error("도토리가 부족합니다: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error("INSUFFICIENT_ACORN", ex.getMessage()));
  }

  /** InvalidDrawTypeException 처리 */
  @ExceptionHandler(InvalidDrawTypeException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidDrawTypeException(
      InvalidDrawTypeException ex, WebRequest request) {
    log.error("유효하지 않은 뽑기 타입: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("INVALID_DRAW_TYPE", ex.getMessage()));
  }

  /** Validation 예외 처리 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.error("유효성 검사 실패: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.<Map<String, String>>builder()
                .success(false)
                .error(
                    ApiResponse.ErrorInfo.builder()
                        .code("VALIDATION_ERROR")
                        .message("입력값 검증에 실패했습니다.")
                        .build())
                .data(errors)
                .build());
  }

  /** IllegalArgumentException 처리 */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.error("잘못된 인자: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("BAD_REQUEST", ex.getMessage()));
  }

  /** 인증 예외 처리 */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
      AuthenticationException ex, WebRequest request) {
    log.error("인증 실패: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("AUTHENTICATION_FAILED", "인증에 실패했습니다."));
  }

  /** 접근 권한 예외 처리 */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    log.error("접근 거부: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("ACCESS_DENIED", "접근 권한이 없습니다."));
  }

  /** 일반 예외 처리 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
    log.error("예상치 못한 오류 발생", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
  }

  /** RuntimeException 처리 */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
      RuntimeException ex, WebRequest request) {
    log.error("런타임 오류 발생: {}", ex.getMessage(), ex);

    // 개발 환경에서는 상세 메시지, 프로덕션에서는 일반 메시지
    String message = isProduction() ? "처리 중 오류가 발생했습니다." : ex.getMessage();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("RUNTIME_ERROR", message));
  }

  /** 프로덕션 환경 여부 확인 */
  private boolean isProduction() {
    String profile = System.getProperty("spring.profiles.active", "");
    return profile.contains("prod");
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<?> handleNoResourceFound(
      NoResourceFoundException ex, HttpServletRequest request) {

    String path = request.getRequestURI();

    // OAuth2 관련 경로나 에러 페이지는 JSON 응답
    if (path.contains("error") || path.contains("oauth2") || path.contains("login")) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Not Found");
      error.put("path", path);
      error.put("message", "요청한 페이지를 찾을 수 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."));
  }
}
