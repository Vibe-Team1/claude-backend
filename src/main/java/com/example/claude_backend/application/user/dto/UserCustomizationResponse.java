package com.example.claude_backend.application.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserCustomizationResponse {
    private List<String> backgrounds;
    private List<String> characters;
}