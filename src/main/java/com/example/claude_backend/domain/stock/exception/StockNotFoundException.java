package com.example.claude_backend.domain.stock.exception;

/**
 * 주식을 찾을 수 없을 때 발생하는 예외
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public class StockNotFoundException extends RuntimeException {

  public StockNotFoundException(String message) {
    super(message);
  }

  public StockNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public static StockNotFoundException withTicker(String ticker) {
    return new StockNotFoundException("종목 코드 '" + ticker + "'에 해당하는 주식을 찾을 수 없습니다.");
  }

  public static StockNotFoundException withId(Long id) {
    return new StockNotFoundException("ID '" + id + "'에 해당하는 주식을 찾을 수 없습니다.");
  }
}
