package com.example.claude_backend.domain.stock.repository;

import com.example.claude_backend.domain.stock.entity.Stock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 주식 리포지토리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

  /**
   * 종목 코드로 주식 조회
   *
   * @param ticker 종목 코드
   * @return 주식 정보
   */
  Optional<Stock> findByTicker(String ticker);

  /**
   * 종목명으로 주식 조회
   *
   * @param name 종목명
   * @return 주식 정보
   */
  Optional<Stock> findByName(String name);

  /**
   * 종목명에 특정 키워드가 포함된 주식 목록 조회
   *
   * @param keyword 검색 키워드
   * @return 주식 목록
   */
  @Query("SELECT s FROM Stock s WHERE s.name LIKE %:keyword% OR s.ticker LIKE %:keyword%")
  List<Stock> findByKeyword(@Param("keyword") String keyword);

  /**
   * 종목 코드 존재 여부 확인
   *
   * @param ticker 종목 코드
   * @return 존재 여부
   */
  boolean existsByTicker(String ticker);
}
