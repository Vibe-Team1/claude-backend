package com.example.claude_backend.application.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawResponse {

  private String characterCode;
  private boolean isNew;
}
