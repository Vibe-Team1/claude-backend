package com.example.claude_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ClaudeBackendApplication 애플리케이션의 메인 클래스
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@SpringBootApplication
@EnableJpaAuditing
public class ClaudeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaudeBackendApplication.class, args);
	}
}