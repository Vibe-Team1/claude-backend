package com.example.claude_backend.domain.user.entity;

import com.example.claude_backend.domain.common.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 사용자 권한 엔티티
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Entity
@Table(
    name = "user_roles",
    indexes = {@Index(name = "idx_user_roles_user_id", columnList = "user_id")},
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_user_roles_user_id_role_name",
          columnNames = {"user_id", "role_name"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRole extends BaseTimeEntity {

  /** 권한 고유 식별자 */
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  /** 사용자 (N:1 관계) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference
  private User user;

  /** 권한 이름 */
  @Enumerated(EnumType.STRING)
  @Column(name = "role_name", nullable = false, length = 50)
  private RoleName roleName;

  /** 사용자 설정 (양방향 관계) */
  void setUser(User user) {
    this.user = user;
  }

  /** 권한 이름 enum */
  public enum RoleName {
    ROLE_USER, // 일반 사용자
    ROLE_ADMIN, // 관리자
    ROLE_PREMIUM // 프리미엄 사용자 (향후 확장)
  }
}
