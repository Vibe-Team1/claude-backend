package com.example.claude_backend.infrastructure.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 및 트랜잭션 설정
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.claude_backend.infrastructure.persistence.jpa"
)
public class JpaConfig {
    // JPA 관련 추가 설정이 필요한 경우 여기에 추가
    // 예: QueryDSL, Custom Repository 구현체 등
}