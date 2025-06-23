package com.example.claude_backend.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizationSelectResponse {

    private String message;
    private String currentBackground;
    private String currentCharacter;
}