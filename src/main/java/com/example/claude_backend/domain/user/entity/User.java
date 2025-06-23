package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 사용자 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_google_sub", columnList = "google_sub"),
        @Index(name = "idx_users_nickname", columnList = "nickname")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_users_google_sub", columnNames = "google_sub")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    /**
     * 사용자 고유 식별자 (UUID)
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    /**
     * Google OAuth Subject ID
     */
    @Column(name = "google_sub", nullable = false, length = 255)
    private String googleSub;

    /**
     * 사용자 이메일
     */
    @Column(nullable = false, length = 255)
    private String email;

    /**
     * 사용자 닉네임 (수정 가능)
     */
    @Column(nullable = false, length = 50)
    private String nickname;

    /**
     * 계정 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 사용자 프로필 (1:1 관계)
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private UserProfile profile;

    /**
     * 사용자 권한 (1:N 관계)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference
    private Set<UserRole> roles = new HashSet<>();

    /**
     * 닉네임 변경
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 계정 상태 변경
     */
    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * 프로필 설정
     */
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null && profile.getUser() != this) {
            profile.setUser(this);
        }
    }

    /**
     * 권한 추가
     */
    public void addRole(UserRole role) {
        this.roles.add(role);
        if (role.getUser() != this) {
            role.setUser(this);
        }
    }

    /**
     * 계정 활성 여부 확인
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * 계정 상태 enum
     */
    public enum UserStatus {
        ACTIVE, // 활성
        INACTIVE, // 비활성
        SUSPENDED // 정지
    }
}