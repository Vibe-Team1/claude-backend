package com.example.claude_backend.application.user.service;

import com.example.claude_backend.application.user.dto.BackgroundRequest;
import com.example.claude_backend.application.user.dto.CharacterRequest;
import com.example.claude_backend.application.user.dto.UserCustomizationResponse;
import com.example.claude_backend.domain.user.entity.UserBackground;
import com.example.claude_backend.domain.user.entity.UserCharacter;
import com.example.claude_backend.domain.user.exception.BackgroundAlreadyOwnedException;
import com.example.claude_backend.domain.user.exception.CharacterAlreadyOwnedException;
import com.example.claude_backend.domain.user.exception.InvalidBackgroundCodeException;
import com.example.claude_backend.domain.user.exception.InvalidCharacterCodeException;
import com.example.claude_backend.domain.user.repository.UserBackgroundRepository;
import com.example.claude_backend.domain.user.repository.UserCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCustomizationServiceImpl implements UserCustomizationService {

    private final UserBackgroundRepository userBackgroundRepository;
    private final UserCharacterRepository userCharacterRepository;

    @Override
    public UserCustomizationResponse getUserCustomization(UUID userId) {
        List<String> backgrounds = userBackgroundRepository.findBackgroundCodesByUserId(userId);
        List<String> characters = userCharacterRepository.findCharacterCodesByUserId(userId);

        return UserCustomizationResponse.builder()
                .backgrounds(backgrounds)
                .characters(characters)
                .build();
    }

    @Override
    @Transactional
    public void addBackground(UUID userId, BackgroundRequest request) {
        String backgroundCode = request.getBackgroundCode();

        // 배경 코드 유효성 검사
        if (!isValidBackgroundCode(backgroundCode)) {
            throw new InvalidBackgroundCodeException(backgroundCode);
        }

        // 이미 보유 중인지 확인
        if (userBackgroundRepository.existsByUserIdAndBackgroundCode(userId, backgroundCode)) {
            throw new BackgroundAlreadyOwnedException(backgroundCode);
        }

        // 배경 추가
        UserBackground userBackground = UserBackground.builder()
                .userId(userId)
                .backgroundCode(backgroundCode)
                .build();

        userBackgroundRepository.save(userBackground);
    }

    @Override
    @Transactional
    public void addCharacter(UUID userId, CharacterRequest request) {
        String characterCode = request.getCharacterCode();

        // 캐릭터 코드 유효성 검사
        if (!isValidCharacterCode(characterCode)) {
            throw new InvalidCharacterCodeException(characterCode);
        }

        // 이미 보유 중인지 확인
        if (userCharacterRepository.existsByUserIdAndCharacterCode(userId, characterCode)) {
            throw new CharacterAlreadyOwnedException(characterCode);
        }

        // 캐릭터 추가
        UserCharacter userCharacter = UserCharacter.builder()
                .userId(userId)
                .characterCode(characterCode)
                .build();

        userCharacterRepository.save(userCharacter);
    }

    private boolean isValidBackgroundCode(String backgroundCode) {
        return "01".equals(backgroundCode) || "02".equals(backgroundCode);
    }

    private boolean isValidCharacterCode(String characterCode) {
        try {
            int code = Integer.parseInt(characterCode);
            return code >= 1 && code <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}