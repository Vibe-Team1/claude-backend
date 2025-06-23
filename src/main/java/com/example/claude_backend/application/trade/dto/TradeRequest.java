package com.example.claude_backend.application.trade.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeRequest {
  private String stockCode;
  private Integer quantity;
  private Double price;
  private String tradeType; // BUY, SELL
}
