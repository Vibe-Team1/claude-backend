package com.example.claude_backend.application.user.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

  @NotNull(message = "친구 ID는 필수입니다")
  private UUID friendId;
}
