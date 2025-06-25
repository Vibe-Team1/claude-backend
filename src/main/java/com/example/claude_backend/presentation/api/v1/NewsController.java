package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.news.dto.NewsCreateRequest;
import com.example.claude_backend.application.news.dto.NewsResponse;
import com.example.claude_backend.application.news.service.NewsService;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 뉴스 API 컨트롤러
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "뉴스 API")
public class NewsController {

  private final NewsService newsService;

  /**
   * 모든 뉴스를 최신순으로 조회합니다.
   *
   * @return 뉴스 목록
   */
  @GetMapping
  @Operation(summary = "뉴스 목록 조회", description = "저장된 모든 뉴스를 최신순으로 조회합니다.")
  public ResponseEntity<ApiResponse<List<NewsResponse>>> getAllNews() {
    log.info("뉴스 목록 조회 API 호출");
    List<NewsResponse> newsList = newsService.getAllNews();
    return ResponseEntity.ok(ApiResponse.success(newsList));
  }

  /**
   * 새로운 뉴스를 생성합니다.
   *
   * @param request 뉴스 생성 요청
   * @return 생성된 뉴스
   */
  @PostMapping
  @Operation(summary = "뉴스 생성", description = "새로운 뉴스를 생성합니다.")
  public ResponseEntity<ApiResponse<NewsResponse>> createNews(
      @Valid @RequestBody NewsCreateRequest request) {
    log.info("뉴스 생성 API 호출: {}", request.getSummary());
    NewsResponse createdNews = newsService.createNews(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdNews));
  }
}
