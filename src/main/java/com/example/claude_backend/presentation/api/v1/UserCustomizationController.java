package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.user.dto.BackgroundRequest;
import com.example.claude_backend.application.user.dto.CharacterRequest;
import com.example.claude_backend.application.user.dto.CustomizationSelectRequest;
import com.example.claude_backend.application.user.dto.CustomizationSelectResponse;
import com.example.claude_backend.application.user.dto.UserCustomizationResponse;
import com.example.claude_backend.application.user.service.UserCustomizationService;
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

import java.util.UUID;

@Slf4j
@Tag(name = "User Customization", description = "사용자 커스터마이제이션 API")
@RestController
@RequestMapping("/api/user/customization")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserCustomizationController {

    private final UserCustomizationService userCustomizationService;

    @Operation(summary = "사용자 커스터마이제이션 조회", description = "현재 로그인된 사용자의 보유 배경과 캐릭터를 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<UserCustomizationResponse>> getUserCustomization(Authentication authentication) {
        UUID userId = SecurityUtil.getCurrentUserId();
        UserCustomizationResponse response = userCustomizationService.getUserCustomization(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "배경 추가", description = "로그인된 사용자에게 배경을 추가합니다")
    @PostMapping("/background")
    public ResponseEntity<ApiResponse<Void>> addBackground(
            Authentication authentication,
            @Valid @RequestBody BackgroundRequest request) {

        UUID userId = SecurityUtil.getCurrentUserId();
        userCustomizationService.addBackground(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    @Operation(summary = "캐릭터 추가", description = "로그인된 사용자에게 캐릭터를 추가합니다")
    @PostMapping("/character")
    public ResponseEntity<ApiResponse<Void>> addCharacter(
            Authentication authentication,
            @Valid @RequestBody CharacterRequest request) {

        UUID userId = SecurityUtil.getCurrentUserId();
        userCustomizationService.addCharacter(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    @Operation(summary = "커스터마이제이션 선택", description = "현재 로그인된 사용자의 배경과 캐릭터를 선택합니다")
    @PatchMapping("/select")
    public ResponseEntity<ApiResponse<CustomizationSelectResponse>> selectCustomization(
            Authentication authentication,
            @Valid @RequestBody CustomizationSelectRequest request) {

        log.debug("커스터마이제이션 선택 요청: {}", request);

        UUID userId = SecurityUtil.getCurrentUserId();
        CustomizationSelectResponse response = userCustomizationService.selectCustomization(userId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}