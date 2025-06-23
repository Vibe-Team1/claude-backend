package com.example.claude_backend.domain.trade.repository;

import com.example.claude_backend.domain.trade.entity.Trade;
import com.example.claude_backend.domain.trade.entity.Trade.TradeStatus;
import com.example.claude_backend.domain.trade.entity.Trade.TradeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 거래 리포지토리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * 사용자별 거래 목록 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 거래 목록
     */
    Page<Trade> findByUserIdOrderByTimestampDesc(UUID userId, Pageable pageable);

    /**
     * 계좌별 거래 목록 조회
     *
     * @param accountId 계좌 ID
     * @param pageable  페이징 정보
     * @return 거래 목록
     */
    Page<Trade> findByAccountIdOrderByTimestampDesc(UUID accountId, Pageable pageable);

    /**
     * 사용자별 특정 주식 거래 목록 조회
     *
     * @param userId   사용자 ID
     * @param stockId  주식 ID
     * @param pageable 페이징 정보
     * @return 거래 목록
     */
    Page<Trade> findByUserIdAndStockIdOrderByTimestampDesc(
            UUID userId, Long stockId, Pageable pageable);

    /**
     * 사용자별 거래 타입별 거래 목록 조회
     *
     * @param userId    사용자 ID
     * @param tradeType 거래 타입
     * @param pageable  페이징 정보
     * @return 거래 목록
     */
    Page<Trade> findByUserIdAndTradeTypeOrderByTimestampDesc(
            UUID userId, TradeType tradeType, Pageable pageable);

    /**
     * 사용자별 거래 상태별 거래 목록 조회
     *
     * @param userId   사용자 ID
     * @param status   거래 상태
     * @param pageable 페이징 정보
     * @return 거래 목록
     */
    Page<Trade> findByUserIdAndStatusOrderByTimestampDesc(
            UUID userId, TradeStatus status, Pageable pageable);

    /**
     * 사용자별 완료된 거래 목록 조회
     *
     * @param userId 사용자 ID
     * @return 완료된 거래 목록
     */
    List<Trade> findByUserIdAndStatusOrderByTimestampDesc(UUID userId, TradeStatus status);

    /**
     * 특정 기간 동안의 사용자 거래 목록 조회
     *
     * @param userId    사용자 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 거래 목록
     */
    @Query("SELECT t FROM Trade t WHERE t.user.id = :userId "
            + "AND t.timestamp BETWEEN :startDate AND :endDate "
            + "ORDER BY t.timestamp DESC")
    List<Trade> findByUserIdAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 사용자별 거래 통계 조회
     *
     * @param userId 사용자 ID
     * @return 거래 통계
     */
    @Query("SELECT COUNT(t) as totalTrades, "
            + "SUM(CASE WHEN t.tradeType = 'BUY' THEN 1 ELSE 0 END) as buyCount, "
            + "SUM(CASE WHEN t.tradeType = 'SELL' THEN 1 ELSE 0 END) as sellCount, "
            + "SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedCount "
            + "FROM Trade t WHERE t.user.id = :userId")
    Object[] getTradeStatistics(@Param("userId") UUID userId);

    /**
     * 주식별 거래 목록 조회
     *
     * @param stockId  주식 ID
     * @param pageable 페이징 정보
     * @return 거래 목록
     */
    Page<Trade> findByStockIdOrderByTimestampDesc(Long stockId, Pageable pageable);
}
