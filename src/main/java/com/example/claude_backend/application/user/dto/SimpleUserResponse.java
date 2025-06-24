package com.example.claude_backend.application.user.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleUserResponse {

  private UUID userId;
  private String nickname;
}
