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
    basePackages = {
      "com.example.claude_backend.infrastructure.persistence.jpa",
      "com.example.claude_backend.domain" // 도메인 전체 포함
    })
public class JpaConfig {}
