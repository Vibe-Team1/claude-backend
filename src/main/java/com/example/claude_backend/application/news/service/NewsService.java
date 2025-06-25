package com.example.claude_backend.application.news.service;

import com.example.claude_backend.application.news.dto.NewsCreateRequest;
import com.example.claude_backend.application.news.dto.NewsResponse;
import java.util.List;

/**
 * 뉴스 서비스 인터페이스
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public interface NewsService {

  /**
   * 모든 뉴스를 최신순으로 조회합니다.
   *
   * @return 뉴스 목록
   */
  List<NewsResponse> getAllNews();

  /**
   * 새로운 뉴스를 생성합니다.
   *
   * @param request 뉴스 생성 요청
   * @return 생성된 뉴스
   */
  NewsResponse createNews(NewsCreateRequest request);
}
