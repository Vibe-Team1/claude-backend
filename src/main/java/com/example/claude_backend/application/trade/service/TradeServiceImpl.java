package com.example.claude_backend.application.trade.service;

import com.example.claude_backend.application.account.service.AccountService;
import com.example.claude_backend.application.trade.dto.PortfolioResponse;
import com.example.claude_backend.application.trade.dto.PortfolioItemResponse;
import com.example.claude_backend.application.trade.dto.TradeRequest;
import com.example.claude_backend.application.trade.dto.TradeResponse;
import com.example.claude_backend.domain.account.entity.Account;
import com.example.claude_backend.domain.account.exception.InsufficientBalanceException;
import com.example.claude_backend.domain.stock.entity.Stock;
import com.example.claude_backend.domain.stock.exception.StockNotFoundException;
import com.example.claude_backend.domain.stock.repository.StockRepository;
import com.example.claude_backend.domain.trade.entity.Trade;
import com.example.claude_backend.domain.trade.entity.Trade.TradeStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.info("거래 실행 시작 - 사용자: {}, 종목: {}, 타입: {}, 수량: {}, 가격: {}",
                userId, request.getStockCode(), request.getTradeType(),
                request.getQuantity(), request.getPrice());

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 프론트엔드에서 전달받은 주식 정보로 Stock 엔티티 생성 또는 조회
        Stock stock = getOrCreateStock(request.getStockCode(), request.getStockName());

        // 3. 계좌 조회
        Account account = accountService.getUserAccount(userId);

        // 4. 거래 타입 결정
        TradeType tradeType = "BUY".equals(request.getTradeType()) ? TradeType.BUY : TradeType.SELL;

        // 5. 거래 실행
        if (tradeType == TradeType.BUY) {
            return executeBuyTrade(user, account, stock, request);
        } else {
            return executeSellTrade(user, account, stock, request);
        }
    }

    /**
     * 주식 코드로 주식을 조회하거나, 없으면 새로 생성
     */
    private Stock getOrCreateStock(String stockCode, String stockName) {
        return stockRepository.findByTicker(stockCode)
                .orElseGet(() -> {
                    // 주식이 없으면 새로 생성
                    Stock newStock = Stock.builder()
                            .ticker(stockCode)
                            .name(stockName != null ? stockName : stockCode)
                            .currentPrice(BigDecimal.ZERO) // 거래 시점의 가격은 request에서 받음
                            .per(BigDecimal.ZERO)
                            .pbr(BigDecimal.ZERO)
                            .build();
                    return stockRepository.save(newStock);
                });
    }

    private TradeResponse executeBuyTrade(User user, Account account, Stock stock, TradeRequest request) {
        BigDecimal totalAmount = BigDecimal.valueOf(request.getPrice())
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        // 잔액 확인
        if (account.getBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("잔액이 부족합니다. 필요: " + totalAmount + ", 보유: " + account.getBalance());
        }

        // 1. 계좌에서 금액 차감
        accountService.withdraw(user.getId(), totalAmount.doubleValue());

        // 2. 사용자 보유 주식 업데이트
        updateUserStock(user, stock, request.getQuantity(), true, BigDecimal.valueOf(request.getPrice()));

        // 3. 거래 기록 저장
        Trade trade = Trade.createTrade(user, account, stock, TradeType.BUY,
                request.getQuantity(), BigDecimal.valueOf(request.getPrice()));
        Trade savedTrade = tradeRepository.save(trade);

        log.info("매수 거래 완료 - 거래 ID: {}, 총액: {}", savedTrade.getId(), totalAmount);

        // 4. 매수 거래는 수익과 도토리가 없으므로 0으로 설정
        return TradeResponse.builder()
                .tradeId(savedTrade.getId())
                .stockCode(savedTrade.getStock().getTicker())
                .stockName(savedTrade.getStock().getName())
                .quantity(savedTrade.getQuantity())
                .price(savedTrade.getPrice().doubleValue())
                .totalAmount(savedTrade.getTotalAmount().doubleValue())
                .tradeType(savedTrade.getTradeType().name())
                .tradeDate(savedTrade.getTimestamp())
                .status(savedTrade.getStatus().name())
                .profit(0.0) // 매수는 수익 없음
                .acornReward(0) // 매수는 도토리 지급 없음
                .build();
    }

    private TradeResponse executeSellTrade(User user, Account account, Stock stock, TradeRequest request) {
        BigDecimal totalAmount = BigDecimal.valueOf(request.getPrice())
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        // 보유 주식 확인
        UserStock userStock = userStockRepository.findByUserAndStock(user, stock)
                .orElseThrow(() -> new RuntimeException("보유한 주식이 없습니다."));

        if (userStock.getQuantity() < request.getQuantity()) {
            throw new RuntimeException(
                    "보유한 주식 수량이 부족합니다. 보유: " + userStock.getQuantity() + ", 매도: " + request.getQuantity());
        }

        // 수익 계산
        BigDecimal averagePrice = userStock.getAveragePrice();
        BigDecimal sellPrice = BigDecimal.valueOf(request.getPrice());
        BigDecimal profitPerShare = sellPrice.subtract(averagePrice);
        BigDecimal totalProfit = profitPerShare.multiply(BigDecimal.valueOf(request.getQuantity()));

        // 1. 계좌에 금액 추가
        accountService.deposit(user.getId(), totalAmount.doubleValue());

        // 2. 수익이 발생한 경우 도토리 지급 (100원당 1개)
        Integer acornReward = 0;
        if (totalProfit.compareTo(BigDecimal.ZERO) > 0) {
            acornReward = totalProfit.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_DOWN).intValue();
            if (acornReward > 0) {
                accountService.addAcorn(user.getId(), acornReward);
                log.info("매도 수익으로 도토리 지급 - 사용자: {}, 수익: {}, 지급 도토리: {}",
                        user.getId(), totalProfit, acornReward);
            }
        }

        // 3. 사용자 보유 주식 업데이트
        updateUserStock(user, stock, request.getQuantity(), false, null);

        // 4. 거래 기록 저장
        Trade trade = Trade.createTrade(user, account, stock, TradeType.SELL,
                request.getQuantity(), BigDecimal.valueOf(request.getPrice()));
        Trade savedTrade = tradeRepository.save(trade);

        log.info("매도 거래 완료 - 거래 ID: {}, 총액: {}, 수익: {}",
                savedTrade.getId(), totalAmount, totalProfit);

        // 5. 수익과 도토리 정보를 포함한 응답 반환
        return TradeResponse.builder()
                .tradeId(savedTrade.getId())
                .stockCode(savedTrade.getStock().getTicker())
                .stockName(savedTrade.getStock().getName())
                .quantity(savedTrade.getQuantity())
                .price(savedTrade.getPrice().doubleValue())
                .totalAmount(savedTrade.getTotalAmount().doubleValue())
                .tradeType(savedTrade.getTradeType().name())
                .tradeDate(savedTrade.getTimestamp())
                .status(savedTrade.getStatus().name())
                .profit(totalProfit.doubleValue())
                .acornReward(acornReward)
                .build();
    }

    private void updateUserStock(User user, Stock stock, Integer quantity, boolean isBuy, BigDecimal price) {
        UserStock userStock = userStockRepository.findByUserAndStock(user, stock)
                .orElse(UserStock.builder()
                        .user(user)
                        .stock(stock)
                        .quantity(0L)
                        .averagePrice(BigDecimal.ZERO)
                        .build());

        if (isBuy) {
            // 매수: 수량 증가, 평균단가 계산
            long newQuantity = userStock.getQuantity() + quantity;
            BigDecimal newAveragePrice;

            if (userStock.getQuantity() == 0) {
                newAveragePrice = price;
            } else {
                BigDecimal totalCost = userStock.getAveragePrice()
                        .multiply(BigDecimal.valueOf(userStock.getQuantity()))
                        .add(price.multiply(BigDecimal.valueOf(quantity)));
                newAveragePrice = totalCost.divide(BigDecimal.valueOf(newQuantity), 2, BigDecimal.ROUND_HALF_UP);
            }

            userStock.setQuantity(newQuantity);
            userStock.setAveragePrice(newAveragePrice);
        } else {
            // 매도: 수량 감소
            long newQuantity = userStock.getQuantity() - quantity;
            if (newQuantity <= 0) {
                // 수량이 0 이하면 삭제
                userStockRepository.delete(userStock);
                return;
            }
            userStock.setQuantity(newQuantity);
        }

        userStockRepository.save(userStock);
        log.info("사용자 보유 주식 업데이트 완료 - 사용자: {}, 종목: {}, 수량: {}",
                user.getId(), stock.getTicker(), userStock.getQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeResponse> getUserTradeHistory(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return tradeRepository.findByUserIdOrderByTimestampDesc(user.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::convertToTradeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeResponse> getStockTradeHistory(String stockCode) {
        Stock stock = stockRepository.findByTicker(stockCode)
                .orElseThrow(() -> new StockNotFoundException("주식을 찾을 수 없습니다."));

        return tradeRepository.findByStockIdOrderByTimestampDesc(stock.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::convertToTradeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TradeResponse> getUserTradeHistoryPaged(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return tradeRepository.findByUserIdOrderByTimestampDesc(user.getId(), pageable)
                .map(this::convertToTradeResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 계좌 잔고 조회
        Account account = accountService.getUserAccount(userId);
        BigDecimal totalBalance = account.getBalance();

        // 보유 주식 목록 조회
        List<UserStock> userStocks = userStockRepository.findByUser(user);

        BigDecimal totalStockValue = BigDecimal.ZERO;
        BigDecimal totalProfitLoss = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        List<PortfolioItemResponse> items = userStocks.stream()
                .map(userStock -> {
                    Stock stock = userStock.getStock();
                    // 현재가가 0이면 평균단가를 사용 (실제로는 프론트엔드에서 최신 가격을 전달받아야 함)
                    BigDecimal currentPrice = stock.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0
                            ? stock.getCurrentPrice()
                            : userStock.getAveragePrice();

                    BigDecimal totalValue = currentPrice.multiply(BigDecimal.valueOf(userStock.getQuantity()));
                    BigDecimal cost = userStock.getAveragePrice().multiply(BigDecimal.valueOf(userStock.getQuantity()));
                    BigDecimal profitLoss = totalValue.subtract(cost);
                    BigDecimal profitLossRate = cost.compareTo(BigDecimal.ZERO) > 0
                            ? profitLoss.divide(cost, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;

                    return PortfolioItemResponse.builder()
                            .stockCode(stock.getTicker())
                            .stockName(stock.getName())
                            .quantity(userStock.getQuantity())
                            .averagePrice(userStock.getAveragePrice())
                            .currentPrice(currentPrice)
                            .totalValue(totalValue)
                            .profitLoss(profitLoss)
                            .profitLossRate(profitLossRate)
                            .build();
                })
                .collect(Collectors.toList());

        // 총계 계산
        for (PortfolioItemResponse item : items) {
            totalStockValue = totalStockValue.add(item.getTotalValue());
            totalProfitLoss = totalProfitLoss.add(item.getProfitLoss());
            totalCost = totalCost.add(item.getAveragePrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal totalPortfolioValue = totalBalance.add(totalStockValue);
        BigDecimal totalProfitLossRate = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? totalProfitLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return PortfolioResponse.builder()
                .totalBalance(totalBalance)
                .totalStockValue(totalStockValue)
                .totalPortfolioValue(totalPortfolioValue)
                .totalProfitLoss(totalProfitLoss)
                .totalProfitLossRate(totalProfitLossRate)
                .items(items)
                .build();
    }

    private TradeResponse convertToTradeResponse(Trade trade) {
        TradeResponse.TradeResponseBuilder builder = TradeResponse.builder()
                .tradeId(trade.getId())
                .stockCode(trade.getStock().getTicker())
                .stockName(trade.getStock().getName())
                .quantity(trade.getQuantity())
                .price(trade.getPrice().doubleValue())
                .totalAmount(trade.getTotalAmount().doubleValue())
                .tradeType(trade.getTradeType().name())
                .tradeDate(trade.getTimestamp())
                .status(trade.getStatus().name());

        // 매도 거래인 경우 수익과 도토리 정보 추가 (거래 내역에서는 실제 수익을 계산할 수 없으므로 null)
        if (trade.getTradeType() == Trade.TradeType.SELL) {
            builder.profit(null) // 거래 내역에서는 실제 수익을 계산할 수 없음
                    .acornReward(null); // 거래 내역에서는 실제 도토리 지급량을 알 수 없음
        } else {
            builder.profit(0.0)
                    .acornReward(0);
        }

        return builder.build();
    }
}
