package com.example.claude_backend.application.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizationSelectRequest {

  @Pattern(regexp = "^(01|02)$", message = "배경 코드는 '01' 또는 '02'여야 합니다")
  private String backgroundCode;

  @Pattern(
      regexp = "^(0[0-9][0-9]|1[0-7][0-9]|180)$",
      message = "캐릭터 코드는 '001'부터 '180'까지의 3자리 숫자여야 합니다")
  private String characterCode;
}
