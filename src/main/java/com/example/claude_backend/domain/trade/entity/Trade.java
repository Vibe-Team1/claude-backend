package com.example.claude_backend.domain.trade.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import com.example.claude_backend.domain.stock.entity.Stock;
import com.example.claude_backend.domain.user.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

/**
 * 거래 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(
    name = "trades",
    indexes = {
      @Index(name = "idx_trades_user_id", columnList = "user_id"),
      @Index(name = "idx_trades_stock_id", columnList = "stock_id"),
      @Index(name = "idx_trades_trade_type", columnList = "trade_type"),
      @Index(name = "idx_trades_created_at", columnList = "created_at")
    })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Trade extends BaseTimeEntity {

  /** 거래 고유 식별자 */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 사용자 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /** 주식 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_id", nullable = false)
  private Stock stock;

  /** 거래 타입 (BUY, SELL) */
  @Enumerated(EnumType.STRING)
  @Column(name = "trade_type", nullable = false, length = 10)
  private TradeType tradeType;

  /** 거래 수량 */
  @Column(nullable = false)
  private Integer quantity;

  /** 거래 가격 */
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal price;

  /** 거래 총액 */
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal totalAmount;

  /** 거래 상태 */
  @Enumerated(EnumType.STRING)
  @Column(name = "trade_status", nullable = false, length = 20)
  private TradeStatus tradeStatus;

  /** 거래 타입 열거형 */
  public enum TradeType {
    BUY,
    SELL
  }

  /** 거래 상태 열거형 */
  public enum TradeStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    FAILED
  }

  /** 거래 생성 */
  public static Trade createTrade(
      User user, Stock stock, TradeType tradeType, Integer quantity, BigDecimal price) {
    BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));

    return Trade.builder()
        .user(user)
        .stock(stock)
        .tradeType(tradeType)
        .quantity(quantity)
        .price(price)
        .totalAmount(totalAmount)
        .tradeStatus(TradeStatus.PENDING)
        .build();
  }

  /** 거래 완료 */
  public void complete() {
    this.tradeStatus = TradeStatus.COMPLETED;
  }

  /** 거래 취소 */
  public void cancel() {
    this.tradeStatus = TradeStatus.CANCELLED;
  }

  /** 거래 실패 */
  public void fail() {
    this.tradeStatus = TradeStatus.FAILED;
  }

  /** 거래 상태 확인 */
  public boolean isCompleted() {
    return TradeStatus.COMPLETED.equals(this.tradeStatus);
  }

  public boolean isPending() {
    return TradeStatus.PENDING.equals(this.tradeStatus);
  }

  public boolean isCancelled() {
    return TradeStatus.CANCELLED.equals(this.tradeStatus);
  }
}
