package com.example.claude_backend.infrastructure.websocket.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Redis 메시지 리스너 (임시 비활성화) Redis Pub/Sub 메시지를 WebSocket으로 브로드캐스트
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
public class RedisMessageListener {
  // 임시로 WebSocket 기능 비활성화
  // TODO: WebSocket 의존성 문제 해결 후 활성화

  public RedisMessageListener() {
    log.info("Redis 메시지 리스너가 임시로 비활성화되었습니다.");
  }
}
