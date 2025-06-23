package com.example.claude_backend.domain.account.entity;

import com.example.claude_backend.domain.account.exception.InsufficientBalanceException;
import com.example.claude_backend.domain.common.BaseTimeEntity;
import com.example.claude_backend.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 계좌 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(name = "accounts", indexes = {
    @Index(name = "idx_accounts_user_id", columnList = "user_id") }, uniqueConstraints = {
        @UniqueConstraint(name = "uk_accounts_user_id", columnNames = "user_id") })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account extends BaseTimeEntity {

  /** 계좌 고유 식별자 */
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  /** 사용자 (1:1 관계) */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  @JsonBackReference
  private User user;

  /** 계좌 잔액 */
  @Column(nullable = false, precision = 19, scale = 2)
  @Builder.Default
  private BigDecimal balance = BigDecimal.ZERO;

  /** 도토리 */
  @Column(nullable = false)
  @Builder.Default
  private Integer acorn = 5;

  /** 잔액 업데이트 */
  public void updateBalance(BigDecimal balance) {
    this.balance = balance;
  }

  /** 도토리 업데이트 */
  public void updateAcorn(Integer acorn) {
    this.acorn = acorn;
  }

  /** 도토리 증가 */
  public void addAcorn(Integer amount) {
    this.acorn += amount;
  }

  /** 도토리 감소 */
  public void subtractAcorn(Integer amount) {
    if (this.acorn < amount) {
      throw new InsufficientBalanceException("도토리가 부족합니다.");
    }
    this.acorn -= amount;
  }

  /** 도토리 확인 */
  public boolean hasSufficientAcorn(Integer amount) {
    return this.acorn >= amount;
  }

  /** 잔액 증가 */
  public void addBalance(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }

  /** 잔액 감소 */
  public void subtractBalance(BigDecimal amount) {
    if (this.balance.compareTo(amount) < 0) {
      throw new InsufficientBalanceException("잔액이 부족합니다.");
    }
    this.balance = this.balance.subtract(amount);
  }

  /** 잔액 확인 */
  public boolean hasSufficientBalance(BigDecimal amount) {
    return this.balance.compareTo(amount) >= 0;
  }
}
