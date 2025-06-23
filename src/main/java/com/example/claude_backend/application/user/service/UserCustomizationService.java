package com.example.claude_backend.application.user.service;

import com.example.claude_backend.application.user.dto.BackgroundRequest;
import com.example.claude_backend.application.user.dto.CharacterRequest;
import com.example.claude_backend.application.user.dto.CustomizationSelectRequest;
import com.example.claude_backend.application.user.dto.CustomizationSelectResponse;
import com.example.claude_backend.application.user.dto.UserCustomizationResponse;

import java.util.UUID;

public interface UserCustomizationService {

    UserCustomizationResponse getUserCustomization(UUID userId);

    void addBackground(UUID userId, BackgroundRequest request);

    void addCharacter(UUID userId, CharacterRequest request);

    CustomizationSelectResponse selectCustomization(UUID userId, CustomizationSelectRequest request);
}