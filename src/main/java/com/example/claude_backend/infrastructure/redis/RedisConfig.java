package com.example.claude_backend.infrastructure.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스 Redis 연결, 직렬화, 메시지 리스너 설정
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Configuration
public class RedisConfig {

  /** Redis Template 설정 JSON 직렬화를 사용하여 객체 저장/조회 */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // ObjectMapper 설정 (LocalDateTime 지원)
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);

    // 직렬화 설정
    GenericJackson2JsonRedisSerializer jsonSerializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);
    StringRedisSerializer stringSerializer = new StringRedisSerializer();

    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.afterPropertiesSet();
    log.info("Redis Template 설정 완료");
    return template;
  }

  /** Redis 메시지 리스너 컨테이너 설정 Pub/Sub 메시지 수신을 위한 설정 */
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);

    // t2.micro 최적화: 스레드 풀 크기 제한
    container.setTaskExecutor(java.util.concurrent.Executors.newFixedThreadPool(4));

    log.info("Redis Message Listener Container 설정 완료");
    return container;
  }
}
