package com.example.claude_backend.infrastructure.websocket;

import com.example.claude_backend.infrastructure.websocket.handler.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * WebSocket 설정 클래스 STOMP 메시지 브로커, 엔드포인트, 전송 설정
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

  /** TaskScheduler 빈 등록 (heartbeat 지원용) */
  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(2);
    scheduler.setThreadNamePrefix("websocket-");
    scheduler.initialize();
    return scheduler;
  }

  /** STOMP 엔드포인트 등록 */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setAllowedOrigins(
            "http://localhost:5173", // 로컬 개발 서버
            "http://localhost:3000", // 프론트엔드 서버
            "http://127.0.0.1:5173", // 로컬 IP 주소
            "http://127.0.0.1:3000" // 프론트엔드 IP 주소
            )
        .addInterceptors(webSocketHandshakeInterceptor)
        .withSockJS(); // SockJS 지원 (폴백)

    log.info("WebSocket STOMP 엔드포인트 등록 완료: /ws");
  }

  /** 메시지 브로커 설정 Redis Pub/Sub을 사용하여 메시지 브로드캐스트 */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 애플리케이션 목적지 접두사 (클라이언트 -> 서버)
    registry.setApplicationDestinationPrefixes("/app");

    // 인메모리 브로커 설정 (Redis Pub/Sub과 함께 사용)
    registry
        .enableSimpleBroker("/topic", "/queue")
        .setHeartbeatValue(new long[] {10000, 10000})
        .setTaskScheduler(taskScheduler()); // TaskScheduler 명시적으로 지정

    log.info("STOMP 메시지 브로커 설정 완료 (인메모리 + Redis Pub/Sub)");
  }

  /** WebSocket 전송 설정 t2.micro 최적화를 위한 설정 */
  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration
        .setMessageSizeLimit(64 * 1024) // 64KB
        .setSendBufferSizeLimit(512 * 1024) // 512KB
        .setSendTimeLimit(20000); // 20초

    log.info("WebSocket 전송 설정 완료 (t2.micro 최적화)");
  }
}
