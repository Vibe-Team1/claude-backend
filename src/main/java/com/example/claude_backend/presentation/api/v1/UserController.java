package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.user.dto.UserResponse;
import com.example.claude_backend.application.user.dto.UserSearchResponse;
import com.example.claude_backend.application.user.dto.UserStockResponse;
import com.example.claude_backend.application.user.dto.UserUpdateRequest;
import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 REST API 컨트롤러
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  /**
   * 현재 로그인한 사용자 정보 조회
   *
   * @return 사용자 정보
   */
  @GetMapping("/me")
  @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
  public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
    log.debug("현재 사용자 정보 조회 요청");

    UUID userId = SecurityUtil.getCurrentUserId();
    UserResponse user = userService.getUserById(userId);

    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * 현재 사용자 정보 수정
   *
   * @param request 수정 요청 정보
   * @return 수정된 사용자 정보
   */
  @PatchMapping("/me")
  @Operation(summary = "내 정보 수정", description = "닉네임, 프로필 이미지, 자기소개를 수정합니다.")
  public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
      @Valid @RequestBody UserUpdateRequest request) {
    log.info("사용자 정보 수정 요청: {}", request);

    UUID userId = SecurityUtil.getCurrentUserId();
    UserResponse updatedUser = userService.updateUser(userId, request);

    return ResponseEntity.ok(ApiResponse.success(updatedUser));
  }

  /**
   * 특정 사용자 정보 조회 (공개 정보만)
   *
   * @param userId 사용자 ID
   * @return 사용자 공개 정보
   */
  @GetMapping("/{userId}")
  @Operation(summary = "사용자 조회", description = "특정 사용자의 공개 정보를 조회합니다.")
  public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID userId) {
    log.debug("사용자 정보 조회 요청. ID: {}", userId);

    UserResponse user = userService.getUserById(userId);
    // TODO: 공개 정보만 필터링하는 로직 추가 필요

    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * 닉네임 중복 확인
   *
   * @param nickname 확인할 닉네임
   * @return 사용 가능 여부
   */
  @GetMapping("/check-nickname")
  @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
  public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkNickname(
      @RequestParam String nickname) {
    log.debug("닉네임 중복 확인: {}", nickname);

    UUID currentUserId = SecurityUtil.getCurrentUserIdOrNull();
    boolean available = userService.isNicknameAvailable(nickname, currentUserId);

    return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
  }

  /**
   * 사용자 검색
   *
   * @param nickname 검색할 닉네임
   * @return 검색 결과
   */
  @GetMapping("/search")
  @Operation(
      summary = "사용자 검색",
      description = "닉네임으로 사용자를 검색합니다. 닉네임, 현재 캐릭터, 계좌 잔액, 보유 캐릭터 개수를 반환합니다.")
  public ResponseEntity<ApiResponse<List<UserSearchResponse>>> searchUsers(
      @RequestParam String nickname) {
    log.debug("사용자 검색 요청: {}", nickname);

    List<UserSearchResponse> results = userService.searchUsersByNickname(nickname);
    return ResponseEntity.ok(ApiResponse.success(results));
  }

  /** 사용자 보유 주식 조회 */
  @GetMapping("/stocks")
  public ResponseEntity<ApiResponse<List<UserStockResponse>>> getUserStocks(
      Authentication authentication) {
    UUID userId = SecurityUtil.getCurrentUserId();
    List<UserStockResponse> userStocks = userService.getUserStocks(userId);

    return ResponseEntity.ok(ApiResponse.success(userStocks));
  }
}
