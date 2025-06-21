package com.example.claude_backend.application.user.dto;


import com.example.claude_backend.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 응답 DTO
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    /**
     * 사용자 ID
     */
    private UUID id;

    /**
     * 이메일
     */
    private String email;

    /**
     * 닉네임
     */
    private String nickname;

    /**
     * 계정 상태
     */
    private String status;

    /**
     * 프로필 정보
     */
    private UserProfileResponse profile;

    /**
     * 권한 목록
     */
    private Set<String> roles;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     *
     * @param user User 엔티티
     * @return UserResponse DTO
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .status(user.getStatus().name())
                .profile(user.getProfile() != null ?
                        UserProfileResponse.from(user.getProfile()) : null)
                .roles(user.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 간단한 사용자 정보만 포함하는 정적 팩토리 메서드
     *
     * @param user User 엔티티
     * @return 간단한 UserResponse DTO
     */
    public static UserResponse simple(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}