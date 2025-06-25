package com.example.claude_backend.infrastructure.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * WebSocket 이벤트 핸들러 (임시 비활성화) 연결, 메시지, 오류 이벤트 처리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Component
public class WebSocketEventHandler {
  // 임시로 WebSocket 기능 비활성화
  // TODO: WebSocket 의존성 문제 해결 후 활성화

  public WebSocketEventHandler() {
    log.info("WebSocket 이벤트 핸들러가 임시로 비활성화되었습니다.");
  }
}
