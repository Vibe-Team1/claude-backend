package com.example.claude_backend.application.user.service;

import com.example.claude_backend.application.user.dto.UserResponse;
import com.example.claude_backend.application.user.dto.UserStockResponse;
import com.example.claude_backend.application.user.dto.UserUpdateRequest;
import com.example.claude_backend.domain.user.entity.User;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public interface UserService {

  /**
   * 사용자 ID로 조회
   *
   * @param userId 사용자 ID
   * @return 사용자 응답 DTO
   */
  UserResponse getUserById(UUID userId);

  /**
   * 이메일로 사용자 조회
   *
   * @param email 이메일
   * @return 사용자 응답 DTO
   */
  UserResponse getUserByEmail(String email);

  /**
   * 이메일로 사용자 Entity 조회 (내부 사용)
   *
   * @param email 이메일
   * @return 사용자 Entity
   */
  User findByEmail(String email);

  /**
   * 사용자 정보 수정
   *
   * @param userId  사용자 ID
   * @param request 수정 요청 DTO
   * @return 수정된 사용자 응답 DTO
   */
  UserResponse updateUser(UUID userId, UserUpdateRequest request);

  /**
   * 닉네임 중복 확인
   *
   * @param nickname      확인할 닉네임
   * @param excludeUserId 제외할 사용자 ID (본인 제외)
   * @return 사용 가능 여부
   */
  boolean isNicknameAvailable(String nickname, UUID excludeUserId);

  /**
   * OAuth 로그인 또는 회원가입
   *
   * @param email           Google 이메일
   * @param googleSub       Google Subject ID
   * @param name            Google 계정 이름
   * @param profileImageUrl Google 프로필 이미지 URL
   * @return 생성 또는 업데이트된 사용자
   */
  User processOAuthLogin(String email, String googleSub, String name, String profileImageUrl);

  /**
   * 사용자 Entity 조회 (내부 사용)
   *
   * @param userId 사용자 ID
   * @return 사용자 Entity
   */
  User getUserEntityById(UUID userId);

  /**
   * 사용자 보유 주식 조회
   *
   * @param userId 사용자 ID
   * @return 보유 주식 목록
   */
  List<UserStockResponse> getUserStocks(UUID userId);
}
