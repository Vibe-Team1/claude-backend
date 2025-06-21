package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 사용자 프로필 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile extends BaseTimeEntity {

    /**
     * 프로필 고유 식별자
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    /**
     * 사용자 (1:1 관계)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    /**
     * 자기소개
     */
    @Column(length = 1000)
    private String bio;

    /**
     * 총 자산 (모의투자용, 단위: 원)
     */
    @Column(name = "total_assets")
    @Builder.Default
    private Long totalAssets = 10000000L; // 기본 1천만원

    /**
     * 룸 레벨 (SNS 꾸미기 레벨)
     */
    @Column(name = "room_level")
    @Builder.Default
    private Integer roomLevel = 1;

    /**
     * 프로필 업데이트
     */
    public void updateProfile(String profileImageUrl, String bio) {
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
        if (bio != null) {
            this.bio = bio;
        }
    }

    /**
     * 총 자산 업데이트
     */
    public void updateTotalAssets(Long totalAssets) {
        this.totalAssets = totalAssets;
    }

    /**
     * 룸 레벨 업
     */
    public void levelUp() {
        this.roomLevel++;
    }

    /**
     * 사용자와 연결 (양방향 관계 설정)
     */
    void setUser(User user) {
        this.user = user;
    }
}