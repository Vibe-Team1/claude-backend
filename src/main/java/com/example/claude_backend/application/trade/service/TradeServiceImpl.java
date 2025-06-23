package com.example.claude_backend.application.trade.service;

import com.example.claude_backend.application.account.service.AccountService;
import com.example.claude_backend.application.trade.dto.TradeRequest;
import com.example.claude_backend.application.trade.dto.TradeResponse;
import com.example.claude_backend.domain.account.entity.Account;
import com.example.claude_backend.domain.account.exception.InsufficientBalanceException;
import com.example.claude_backend.domain.stock.entity.Stock;
import com.example.claude_backend.domain.stock.exception.StockNotFoundException;
import com.example.claude_backend.domain.stock.repository.StockRepository;
import com.example.claude_backend.domain.trade.entity.Trade;
import com.example.claude_backend.domain.trade.entity.Trade.TradeType;
import com.example.claude_backend.domain.trade.repository.TradeRepository;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.entity.UserStock;
import com.example.claude_backend.domain.user.exception.UserNotFoundException;
import com.example.claude_backend.domain.user.repository.UserRepository;
import com.example.claude_backend.domain.user.repository.UserStockRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

  private final TradeRepository tradeRepository;
  private final StockRepository stockRepository;
  private final UserRepository userRepository;
  private final UserStockRepository userStockRepository;
  private final AccountService accountService;

  @Override
  public TradeResponse executeTrade(TradeRequest request, UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

    Stock stock =
        stockRepository
            .findByTicker(request.getStockCode())
            .orElseThrow(() -> new StockNotFoundException("주식을 찾을 수 없습니다."));

    Account account = accountService.getUserAccount(userId);
    BigDecimal totalAmount =
        BigDecimal.valueOf(request.getQuantity()).multiply(BigDecimal.valueOf(request.getPrice()));

    TradeType tradeType = "BUY".equals(request.getTradeType()) ? TradeType.BUY : TradeType.SELL;

    if (tradeType == TradeType.BUY) {
      // 매수 로직
      if (account.getBalance().compareTo(totalAmount) < 0) {
        throw new InsufficientBalanceException("잔액이 부족합니다.");
      }

      // 계좌에서 금액 차감
      accountService.withdraw(userId, totalAmount.doubleValue());

      // 사용자 보유 주식 업데이트
      updateUserStock(user, stock, request.getQuantity(), true);

    } else {
      // 매도 로직
      UserStock userStock =
          userStockRepository
              .findByUserAndStock(user, stock)
              .orElseThrow(() -> new RuntimeException("보유한 주식이 없습니다."));

      if (userStock.getQuantity() < request.getQuantity()) {
        throw new RuntimeException("보유한 주식 수량이 부족합니다.");
      }

      // 계좌에 금액 추가
      accountService.deposit(userId, totalAmount.doubleValue());

      // 사용자 보유 주식 업데이트
      updateUserStock(user, stock, request.getQuantity(), false);
    }

    // 거래 기록 저장
    Trade trade =
        Trade.createTrade(
            user, stock, tradeType, request.getQuantity(), BigDecimal.valueOf(request.getPrice()));
    trade.complete();

    Trade savedTrade = tradeRepository.save(trade);

    return convertToTradeResponse(savedTrade);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TradeResponse> getUserTradeHistory(UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

    return tradeRepository
        .findByUserIdOrderByCreatedAtDesc(user.getId(), Pageable.unpaged())
        .getContent()
        .stream()
        .map(this::convertToTradeResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TradeResponse> getStockTradeHistory(String stockCode) {
    Stock stock =
        stockRepository
            .findByTicker(stockCode)
            .orElseThrow(() -> new StockNotFoundException("주식을 찾을 수 없습니다."));

    // 주식별 거래 내역을 조회하는 메서드가 없으므로 임시로 빈 리스트 반환
    return List.of();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TradeResponse> getUserTradeHistoryPaged(UUID userId, Pageable pageable) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

    return tradeRepository
        .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
        .map(this::convertToTradeResponse);
  }

  private void updateUserStock(User user, Stock stock, Integer quantity, boolean isBuy) {
    UserStock userStock =
        userStockRepository
            .findByUserAndStock(user, stock)
            .orElse(UserStock.builder().user(user).stock(stock).quantity(0L).build());

    if (isBuy) {
      userStock.setQuantity(userStock.getQuantity() + quantity);
    } else {
      userStock.setQuantity(userStock.getQuantity() - quantity);
    }

    if (userStock.getQuantity() > 0) {
      userStockRepository.save(userStock);
    } else {
      userStockRepository.delete(userStock);
    }
  }

  private TradeResponse convertToTradeResponse(Trade trade) {
    return TradeResponse.builder()
        .tradeId(Long.valueOf(trade.getId().toString()))
        .stockCode(trade.getStock().getTicker())
        .stockName(trade.getStock().getName())
        .quantity(trade.getQuantity())
        .price(trade.getPrice().doubleValue())
        .totalAmount(trade.getTotalAmount().doubleValue())
        .tradeType(trade.getTradeType().name())
        .tradeDate(trade.getCreatedAt())
        .status(trade.getTradeStatus().name())
        .build();
  }
}
