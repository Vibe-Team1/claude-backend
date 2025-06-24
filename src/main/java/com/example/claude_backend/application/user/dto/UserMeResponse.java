package com.example.claude_backend.application.user.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 현재 로그인한 사용자의 모든 정보를 포함하는 응답 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMeResponse {

    // users 테이블 정보
    private UUID userId;
    private String email;
    private String nickname;
    private String status;

    // user_profiles 테이블 정보
    private String currentCharacterCode;
    private String currentBackgroundCode;
    private String profileImageUrl;
    private String bio;
    private Long totalAssets;
    private Integer roomLevel;

    // accounts 테이블 정보
    private BigDecimal balance;
    private Integer acorn;

    // user_characters 테이블 정보
    private List<String> characterCodes;
}