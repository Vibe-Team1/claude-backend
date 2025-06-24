package com.example.claude_backend.application.trade.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioItemResponse {
  private String stockCode; // 종목 코드
  private String stockName; // 종목명
  private Long quantity; // 보유 수량
  private BigDecimal averagePrice; // 평균단가
  private BigDecimal currentPrice; // 현재가
  private BigDecimal totalValue; // 평가금액
  private BigDecimal profitLoss; // 손익
  private BigDecimal profitLossRate; // 손익률
}
