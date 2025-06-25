package com.example.claude_backend.domain.news.repository;

import com.example.claude_backend.domain.news.entity.News;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 뉴스 리포지토리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {

  /**
   * 최신순으로 모든 뉴스를 조회합니다.
   *
   * @return 최신순 뉴스 목록
   */
  @Query("SELECT n FROM News n ORDER BY n.createdAt DESC")
  List<News> findAllByOrderByCreatedAtDesc();
}
