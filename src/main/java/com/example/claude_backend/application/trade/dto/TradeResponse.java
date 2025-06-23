package com.example.claude_backend.application.trade.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TradeResponse {
  private Long tradeId;
  private String stockCode;
  private String stockName;
  private Integer quantity;
  private Double price;
  private Double totalAmount;
  private String tradeType;
  private LocalDateTime tradeDate;
  private String status;
}
