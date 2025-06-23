package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import com.example.claude_backend.domain.stock.entity.Stock;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 사용자 보유 주식 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(
    name = "user_stocks",
    indexes = {
      @Index(name = "idx_user_stocks_user_id", columnList = "user_id"),
      @Index(name = "idx_user_stocks_stock_id", columnList = "stock_id")
    },
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_user_stocks_user_id_stock_id",
          columnNames = {"user_id", "stock_id"})
    })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserStock extends BaseTimeEntity {

  /** 사용자 보유 주식 고유 식별자 */
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  /** 사용자 (N:1 관계) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference
  private User user;

  /** 주식 (N:1 관계) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_id", nullable = false)
  @JsonBackReference
  private Stock stock;

  /** 보유 수량 */
  @Column(nullable = false)
  private Long quantity;

  /** 평균 매수가 */
  @Column(precision = 19, scale = 2)
  private BigDecimal averagePrice;

  /** 수량 증가 */
  public void addQuantity(Long amount) {
    this.quantity += amount;
  }

  /** 수량 감소 */
  public void subtractQuantity(Long amount) {
    if (this.quantity < amount) {
      throw new IllegalArgumentException("보유 수량보다 많은 수량을 차감할 수 없습니다.");
    }
    this.quantity -= amount;
  }

  /** 평균 매수가 업데이트 */
  public void updateAveragePrice(BigDecimal newAveragePrice) {
    this.averagePrice = newAveragePrice;
  }

  /** 보유 주식 평가 금액 계산 */
  public BigDecimal getTotalValue() {
    if (stock != null && stock.getCurrentPrice() != null) {
      return stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
    }
    return BigDecimal.ZERO;
  }

  /** 수익률 계산 */
  public BigDecimal getProfitRate() {
    if (averagePrice != null
        && averagePrice.compareTo(BigDecimal.ZERO) > 0
        && stock != null
        && stock.getCurrentPrice() != null) {
      return stock
          .getCurrentPrice()
          .subtract(averagePrice)
          .divide(averagePrice, 4, BigDecimal.ROUND_HALF_UP)
          .multiply(BigDecimal.valueOf(100));
    }
    return BigDecimal.ZERO;
  }
}
