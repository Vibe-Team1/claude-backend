package com.example.claude_backend.domain.user.repository;

import com.example.claude_backend.domain.stock.entity.Stock;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.entity.UserStock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 사용자 보유 주식 리포지토리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Repository
public interface UserStockRepository extends JpaRepository<UserStock, UUID> {

  /** 사용자로 보유 주식 목록 조회 */
  List<UserStock> findByUserOrderByStockNameAsc(User user);

  /** 사용자로 보유 주식 목록 조회 (간단한 버전) */
  List<UserStock> findByUser(User user);

  /** 사용자와 주식으로 보유 주식 조회 */
  Optional<UserStock> findByUserAndStock(User user, Stock stock);

  /** 사용자 ID와 주식 ID로 보유 주식 조회 */
  Optional<UserStock> findByUserIdAndStockId(UUID userId, Long stockId);

  /** 사용자 ID와 종목 코드로 보유 주식 조회 */
  @Query(
      "SELECT us FROM UserStock us JOIN us.stock s WHERE us.user.id = :userId AND s.ticker = :ticker")
  Optional<UserStock> findByUserIdAndTicker(
      @Param("userId") UUID userId, @Param("ticker") String ticker);

  /** 사용자로 보유 주식 수량 조회 */
  @Query("SELECT COUNT(us) FROM UserStock us WHERE us.user = :user AND us.quantity > 0")
  Long countByUserAndQuantityGreaterThanZero(@Param("user") User user);

  /** 사용자로 총 보유 주식 평가 금액 조회 */
  @Query(
      "SELECT SUM(us.quantity * s.currentPrice) FROM UserStock us JOIN us.stock s WHERE us.user = :user")
  Double getTotalStockValueByUser(@Param("user") User user);
}
