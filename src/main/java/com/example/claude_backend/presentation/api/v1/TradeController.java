package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.trade.dto.TradeRequest;
import com.example.claude_backend.application.trade.dto.TradeResponse;
import com.example.claude_backend.application.trade.service.TradeService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

  private final TradeService tradeService;

  @PostMapping
  public ResponseEntity<ApiResponse<TradeResponse>> executeTrade(
      @RequestBody TradeRequest request, Authentication authentication) {

    UUID userId = SecurityUtil.getCurrentUserId();
    TradeResponse response = tradeService.executeTrade(request, userId);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/history")
  public ResponseEntity<ApiResponse<List<TradeResponse>>> getUserTradeHistory(
      Authentication authentication) {

    UUID userId = SecurityUtil.getCurrentUserId();
    List<TradeResponse> history = tradeService.getUserTradeHistory(userId);

    return ResponseEntity.ok(ApiResponse.success(history));
  }

  @GetMapping("/stock/{stockCode}/history")
  public ResponseEntity<ApiResponse<List<TradeResponse>>> getStockTradeHistory(
      @PathVariable String stockCode) {

    List<TradeResponse> history = tradeService.getStockTradeHistory(stockCode);

    return ResponseEntity.ok(ApiResponse.success(history));
  }
}
