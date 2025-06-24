package com.example.claude_backend.application.user.service;

import com.example.claude_backend.application.user.dto.BackgroundRequest;
import com.example.claude_backend.application.user.dto.CharacterRequest;
import com.example.claude_backend.application.user.dto.CustomizationSelectRequest;
import com.example.claude_backend.application.user.dto.CustomizationSelectResponse;
import com.example.claude_backend.application.user.dto.UserCustomizationResponse;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.entity.UserBackground;
import com.example.claude_backend.domain.user.entity.UserCharacter;
import com.example.claude_backend.domain.user.exception.BackgroundAlreadyOwnedException;
import com.example.claude_backend.domain.user.exception.BackgroundNotOwnedException;
import com.example.claude_backend.domain.user.exception.CharacterAlreadyOwnedException;
import com.example.claude_backend.domain.user.exception.CharacterNotOwnedException;
import com.example.claude_backend.domain.user.exception.InvalidBackgroundCodeException;
import com.example.claude_backend.domain.user.exception.InvalidCharacterCodeException;
import com.example.claude_backend.domain.user.repository.UserBackgroundRepository;
import com.example.claude_backend.domain.user.repository.UserCharacterRepository;
import com.example.claude_backend.domain.user.repository.UserRepository;
import com.example.claude_backend.infrastructure.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCustomizationServiceImpl implements UserCustomizationService {

    private final UserBackgroundRepository userBackgroundRepository;
    private final UserCharacterRepository userCharacterRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public UserCustomizationResponse getUserCustomization(UUID userId) {
        List<String> backgrounds = userBackgroundRepository.findBackgroundCodesByUserId(userId);
        List<String> characters = userCharacterRepository.findCharacterCodesByUserId(userId);

        // 배경 URL 목록 생성
        List<String> backgroundUrls = backgrounds.stream()
                .map(s3Service::getBackgroundUrl)
                .collect(Collectors.toList());

        // 캐릭터 URL 목록 생성
        List<String> characterUrls = characters.stream()
                .map(s3Service::getCharacterUrl)
                .collect(Collectors.toList());

        return UserCustomizationResponse.builder()
                .backgrounds(backgrounds)
                .characters(characters)
                .backgroundUrls(backgroundUrls)
                .characterUrls(characterUrls)
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

    @Override
    @Transactional
    public CustomizationSelectResponse selectCustomization(UUID userId, CustomizationSelectRequest request) {
        log.debug("커스터마이제이션 선택 요청. userId: {}, backgroundCode: {}, characterCode: {}",
                userId, request.getBackgroundCode(), request.getCharacterCode());

        // 사용자 조회 (프로필 포함)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        if (user.getProfile() == null) {
            throw new RuntimeException("사용자 프로필을 찾을 수 없습니다: " + userId);
        }

        // 배경 코드 검증
        if (request.getBackgroundCode() != null) {
            if (!userBackgroundRepository.existsByUserIdAndBackgroundCode(userId, request.getBackgroundCode())) {
                throw new BackgroundNotOwnedException(userId, request.getBackgroundCode());
            }
        }

        // 캐릭터 코드 검증
        if (request.getCharacterCode() != null) {
            if (!userCharacterRepository.existsByUserIdAndCharacterCode(userId, request.getCharacterCode())) {
                throw new CharacterNotOwnedException(userId, request.getCharacterCode());
            }
        }

        // 프로필 업데이트
        user.getProfile().updateCustomization(request.getBackgroundCode(), request.getCharacterCode());
        userRepository.save(user);

        log.info("커스터마이제이션 선택 완료. userId: {}, backgroundCode: {}, characterCode: {}",
                userId, user.getProfile().getCurrentBackgroundCode(), user.getProfile().getCurrentCharacterCode());

        return CustomizationSelectResponse.builder()
                .backgroundCode(user.getProfile().getCurrentBackgroundCode())
                .characterCode(user.getProfile().getCurrentCharacterCode())
                .build();
    }

    private boolean isValidBackgroundCode(String backgroundCode) {
        return backgroundCode != null && backgroundCode.matches("^(01|02)$");
    }

    private boolean isValidCharacterCode(String characterCode) {
        return characterCode != null && characterCode.matches("^(0[0-9][0-9]|1[0-7][0-9]|180)$");
    }
}