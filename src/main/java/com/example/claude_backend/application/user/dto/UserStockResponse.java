package com.example.claude_backend.application.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStockResponse {
  private String stockCode;
  private String stockName;
  private Integer quantity;
  private Double averagePrice;
  private Double currentPrice;
  private Double totalValue;
  private Double profitLoss;
  private Double profitLossRate;
}
