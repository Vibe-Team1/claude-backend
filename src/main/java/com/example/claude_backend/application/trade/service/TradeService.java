package com.example.claude_backend.application.trade.service;

import com.example.claude_backend.application.trade.dto.PortfolioResponse;
import com.example.claude_backend.application.trade.dto.TradeRequest;
import com.example.claude_backend.application.trade.dto.TradeResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradeService {
    TradeResponse executeTrade(TradeRequest request, UUID userId);

    List<TradeResponse> getUserTradeHistory(UUID userId);

    List<TradeResponse> getStockTradeHistory(String stockCode);

    Page<TradeResponse> getUserTradeHistoryPaged(UUID userId, Pageable pageable);

    PortfolioResponse getPortfolio(UUID userId);
}
