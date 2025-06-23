package com.example.claude_backend.domain.account.exception;

/**
 * 잔액 부족 예외
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public class InsufficientBalanceException extends RuntimeException {

  public InsufficientBalanceException(String message) {
    super(message);
  }

  public InsufficientBalanceException(String message, Throwable cause) {
    super(message, cause);
  }
}
