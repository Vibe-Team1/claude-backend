package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.shop.dto.DrawRequest;
import com.example.claude_backend.application.shop.dto.DrawResponse;
import com.example.claude_backend.application.shop.service.ShopService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
@Tag(name = "Shop", description = "상점 API")
@SecurityRequirement(name = "bearerAuth")
public class ShopController {

    private final ShopService shopService;

    @Operation(summary = "캐릭터 뽑기", description = "도토리를 사용하여 캐릭터를 뽑습니다.")
    @PostMapping("/draw")
    public ResponseEntity<ApiResponse<DrawResponse>> drawCharacter(
            Authentication authentication,
            @Valid @RequestBody DrawRequest request) {

        log.debug("캐릭터 뽑기 요청: {}", request);

        UUID userId = SecurityUtil.getCurrentUserId();
        DrawResponse response = shopService.drawCharacter(userId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}