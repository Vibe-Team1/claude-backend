package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.stock.dto.StockResponse;
import com.example.claude_backend.application.stock.service.StockService;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

  private final StockService stockService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<StockResponse>>> getAllStocks() {
    List<StockResponse> stocks = stockService.getAllStocks();
    return ResponseEntity.ok(ApiResponse.success(stocks));
  }

  @GetMapping("/{stockCode}")
  public ResponseEntity<ApiResponse<StockResponse>> getStockByCode(@PathVariable String stockCode) {
    StockResponse stock = stockService.getStockByCode(stockCode);
    return ResponseEntity.ok(ApiResponse.success(stock));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<List<StockResponse>>> searchStocks(
      @RequestParam String keyword) {
    List<StockResponse> stocks = stockService.searchStocks(keyword);
    return ResponseEntity.ok(ApiResponse.success(stocks));
  }

  @GetMapping("/sector/{sector}")
  public ResponseEntity<ApiResponse<List<StockResponse>>> getStocksBySector(
      @PathVariable String sector) {
    List<StockResponse> stocks = stockService.getStocksBySector(sector);
    return ResponseEntity.ok(ApiResponse.success(stocks));
  }
}
