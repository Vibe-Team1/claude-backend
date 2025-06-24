package com.example.claude_backend.application.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawRequest {

  @NotNull(message = "뽑기 타입은 필수입니다")
  private String type; // NORMAL, PREMIUM, SUPREME
}
