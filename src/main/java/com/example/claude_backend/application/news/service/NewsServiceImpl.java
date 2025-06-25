package com.example.claude_backend.application.news.service;

import com.example.claude_backend.application.news.dto.NewsCreateRequest;
import com.example.claude_backend.application.news.dto.NewsResponse;
import com.example.claude_backend.application.news.mapper.NewsMapper;
import com.example.claude_backend.domain.news.entity.News;
import com.example.claude_backend.domain.news.repository.NewsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 뉴스 서비스 구현체
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {

  private final NewsRepository newsRepository;
  private final NewsMapper newsMapper;

  @Override
  public List<NewsResponse> getAllNews() {
    log.info("모든 뉴스 조회 요청");
    List<News> newsList = newsRepository.findAllByOrderByCreatedAtDesc();
    List<NewsResponse> response = newsMapper.toResponseList(newsList);
    log.info("뉴스 조회 완료: {}건", response.size());
    return response;
  }

  @Override
  @Transactional
  public NewsResponse createNews(NewsCreateRequest request) {
    log.info("뉴스 생성 요청: {}", request.getSummary());

    News news = News.builder().summary(request.getSummary()).build();

    News savedNews = newsRepository.save(news);
    NewsResponse response = newsMapper.toResponse(savedNews);

    log.info("뉴스 생성 완료: ID={}", response.getId());
    return response;
  }
}
