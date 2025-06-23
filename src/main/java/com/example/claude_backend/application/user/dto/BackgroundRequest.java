package com.example.claude_backend.application.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class BackgroundRequest {

    @NotBlank(message = "배경 코드는 필수입니다")
    @Pattern(regexp = "^(01|02)$", message = "배경 코드는 '01' 또는 '02'여야 합니다")
    private String backgroundCode;
}