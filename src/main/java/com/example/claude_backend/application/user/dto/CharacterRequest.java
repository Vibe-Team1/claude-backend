package com.example.claude_backend.application.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CharacterRequest {

    @NotBlank(message = "캐릭터 코드는 필수입니다")
    @Pattern(regexp = "^(0[0-9][0-9]|1[0-7][0-9]|180)$", message = "캐릭터 코드는 '001'부터 '180'까지의 3자리 숫자여야 합니다")
    private String characterCode;
}