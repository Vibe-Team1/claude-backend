package com.example.claude_backend.application.trade.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioResponse {
    private BigDecimal totalBalance; // 총 현금 잔고
    private BigDecimal totalStockValue; // 총 주식 평가금액
    private BigDecimal totalPortfolioValue; // 총 포트폴리오 가치
    private BigDecimal totalProfitLoss; // 총 손익
    private BigDecimal totalProfitLossRate; // 총 손익률
    private List<PortfolioItemResponse> items; // 보유 주식 목록
}