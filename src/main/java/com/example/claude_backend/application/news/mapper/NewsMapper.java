package com.example.claude_backend.application.news.mapper;

import com.example.claude_backend.application.news.dto.NewsResponse;
import com.example.claude_backend.domain.news.entity.News;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 뉴스 매퍼
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Mapper(componentModel = "spring")
public interface NewsMapper {

  NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

  /**
   * News 엔티티를 NewsResponse DTO로 변환
   *
   * @param news 뉴스 엔티티
   * @return 뉴스 응답 DTO
   */
  NewsResponse toResponse(News news);

  /**
   * News 엔티티 리스트를 NewsResponse DTO 리스트로 변환
   *
   * @param newsList 뉴스 엔티티 리스트
   * @return 뉴스 응답 DTO 리스트
   */
  List<NewsResponse> toResponseList(List<News> newsList);
}
