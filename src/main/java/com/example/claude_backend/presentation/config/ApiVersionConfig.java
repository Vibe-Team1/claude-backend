package com.example.claude_backend.presentation.config;

import org.springframework.context.annotation.Configuration;

/** API 버전 관리 설정 현재 v1을 기본으로 하고, 추후 v2, v3 확장을 고려한 구조 */
@Configuration
public class ApiVersionConfig {

  public static final String API_V1_BASE_PATH = "/api/v1";
  public static final String API_V2_BASE_PATH = "/api/v2"; // 향후 확장용
  public static final String API_V3_BASE_PATH = "/api/v3"; // 향후 확장용

  /** 현재 활성화된 API 버전 */
  public static final String CURRENT_API_VERSION = "v1";

  /** API 버전별 기본 경로 반환 */
  public static String getBasePath(String version) {
    return switch (version) {
      case "v1" -> API_V1_BASE_PATH;
      case "v2" -> API_V2_BASE_PATH;
      case "v3" -> API_V3_BASE_PATH;
      default -> API_V1_BASE_PATH;
    };
  }

  /** 현재 API 버전의 기본 경로 반환 */
  public static String getCurrentBasePath() {
    return getBasePath(CURRENT_API_VERSION);
  }
}
