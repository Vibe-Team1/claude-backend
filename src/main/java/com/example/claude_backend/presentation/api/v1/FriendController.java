package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.user.dto.FriendRequest;
import com.example.claude_backend.application.user.dto.SimpleUserResponse;
import com.example.claude_backend.application.user.service.FriendService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Friend", description = "친구 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "전체 사용자 목록 조회", description = "전체 사용자 목록을 조회합니다. (자기 자신 제외)")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<SimpleUserResponse>>> getAllUsers(Authentication authentication) {
        log.debug("전체 사용자 목록 조회 요청");

        UUID currentUserId = SecurityUtil.getCurrentUserId();
        List<SimpleUserResponse> users = friendService.getAllUsers(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "친구 목록 조회", description = "현재 로그인한 사용자의 친구 목록을 조회합니다.")
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<SimpleUserResponse>>> getFriends(Authentication authentication) {
        log.debug("친구 목록 조회 요청");

        UUID userId = SecurityUtil.getCurrentUserId();
        List<SimpleUserResponse> friends = friendService.getFriends(userId);

        return ResponseEntity.ok(ApiResponse.success(friends));
    }

    @Operation(summary = "친구 추가", description = "다른 사용자를 친구로 추가합니다.")
    @PostMapping("/friends")
    public ResponseEntity<ApiResponse<Void>> addFriend(
            Authentication authentication,
            @Valid @RequestBody FriendRequest request) {

        log.debug("친구 추가 요청: {}", request);

        UUID userId = SecurityUtil.getCurrentUserId();
        friendService.addFriend(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }
}