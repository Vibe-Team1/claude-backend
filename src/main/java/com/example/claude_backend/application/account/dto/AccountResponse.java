package com.example.claude_backend.application.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountResponse {
  private UUID accountId;
  private UUID userId;
  private BigDecimal balance;
  private Integer acorn;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
