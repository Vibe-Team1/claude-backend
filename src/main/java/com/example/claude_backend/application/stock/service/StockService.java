package com.example.claude_backend.application.stock.service;

import com.example.claude_backend.application.stock.dto.StockResponse;
import java.util.List;

public interface StockService {
  List<StockResponse> getAllStocks();

  StockResponse getStockByCode(String stockCode);

  List<StockResponse> searchStocks(String keyword);

  List<StockResponse> getStocksBySector(String sector);
}
