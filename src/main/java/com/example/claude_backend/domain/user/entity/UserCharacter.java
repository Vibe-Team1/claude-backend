package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_characters", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "character_code" })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCharacter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "character_code", nullable = false, length = 3)
    private String characterCode;

    @Column(name = "acquired_at", nullable = false)
    private LocalDateTime acquiredAt;

    @Builder
    public UserCharacter(UUID userId, String characterCode) {
        this.userId = userId;
        this.characterCode = characterCode;
        this.acquiredAt = LocalDateTime.now();
    }
}