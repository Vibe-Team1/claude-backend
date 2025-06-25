package com.example.claude_backend.infrastructure.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Redis 메시지 리스너 설정 (임시 비활성화) Redis Pub/Sub을 통한 WebSocket 메시지 브로드캐스트
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Configuration
public class RedisMessageListenerConfig {
  // 임시로 WebSocket 기능 비활성화
  // TODO: WebSocket 의존성 문제 해결 후 활성화

  public RedisMessageListenerConfig() {
    log.info("Redis 메시지 리스너 설정이 임시로 비활성화되었습니다.");
  }
}
