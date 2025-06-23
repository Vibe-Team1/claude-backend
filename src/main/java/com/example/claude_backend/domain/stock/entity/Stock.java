package com.example.claude_backend.domain.stock.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

/**
 * 주식 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(
    name = "stocks",
    indexes = {
      @Index(name = "idx_stocks_ticker", columnList = "ticker"),
      @Index(name = "idx_stocks_name", columnList = "name")
    },
    uniqueConstraints = {@UniqueConstraint(name = "uk_stocks_ticker", columnNames = "ticker")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock extends BaseTimeEntity {

  /** 주식 고유 식별자 */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 종목 코드 */
  @Column(nullable = false, unique = true, length = 20)
  private String ticker;

  /** 종목명 */
  @Column(nullable = false, length = 100)
  private String name;

  /** 현재가 */
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal currentPrice;

  /** PER (주가수익비율) */
  @Column(precision = 19, scale = 2)
  private BigDecimal per;

  /** PBR (주가순자산비율) */
  @Column(precision = 19, scale = 2)
  private BigDecimal pbr;

  /** 거래일 */
  @Column private LocalDate tradeDate;

  /** 거래시간 */
  @Column private LocalTime tradeTime;

  /** 가격 업데이트 */
  public void updatePrice(BigDecimal newPrice) {
    this.currentPrice = newPrice;
  }

  /** 거래 정보 업데이트 */
  public void updateTradeInfo(LocalDate tradeDate, LocalTime tradeTime) {
    this.tradeDate = tradeDate;
    this.tradeTime = tradeTime;
  }

  /** 재무 정보 업데이트 */
  public void updateFinancialInfo(BigDecimal per, BigDecimal pbr) {
    this.per = per;
    this.pbr = pbr;
  }
}
