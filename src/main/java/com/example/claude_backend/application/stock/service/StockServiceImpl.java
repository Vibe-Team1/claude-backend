package com.example.claude_backend.application.stock.service;

import com.example.claude_backend.application.stock.dto.StockResponse;
import com.example.claude_backend.domain.stock.entity.Stock;
import com.example.claude_backend.domain.stock.exception.StockNotFoundException;
import com.example.claude_backend.domain.stock.repository.StockRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

  private final StockRepository stockRepository;

  @Override
  public List<StockResponse> getAllStocks() {
    return stockRepository.findAll().stream()
        .map(this::convertToStockResponse)
        .collect(Collectors.toList());
  }

  @Override
  public StockResponse getStockByCode(String stockCode) {
    Stock stock =
        stockRepository
            .findByTicker(stockCode)
            .orElseThrow(() -> new StockNotFoundException("주식을 찾을 수 없습니다."));

    return convertToStockResponse(stock);
  }

  @Override
  public List<StockResponse> searchStocks(String keyword) {
    return stockRepository.findByKeyword(keyword).stream()
        .map(this::convertToStockResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<StockResponse> getStocksBySector(String sector) {
    // Stock 엔티티에 sector 필드가 없으므로 임시로 빈 리스트 반환
    return List.of();
  }

  private StockResponse convertToStockResponse(Stock stock) {
    return StockResponse.builder()
        .code(stock.getTicker())
        .name(stock.getName())
        .currentPrice(stock.getCurrentPrice().doubleValue())
        .changeRate(0.0) // Stock 엔티티에 해당 필드가 없음
        .changeAmount(0.0) // Stock 엔티티에 해당 필드가 없음
        .volume(0L) // Stock 엔티티에 해당 필드가 없음
        .marketCap(0.0) // Stock 엔티티에 해당 필드가 없음
        .sector("") // Stock 엔티티에 해당 필드가 없음
        .build();
  }
}
