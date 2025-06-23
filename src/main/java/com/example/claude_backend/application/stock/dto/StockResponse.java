package com.example.claude_backend.application.stock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockResponse {
  private String code;
  private String name;
  private Double currentPrice;
  private Double changeRate;
  private Double changeAmount;
  private Long volume;
  private Double marketCap;
  private String sector;
}
