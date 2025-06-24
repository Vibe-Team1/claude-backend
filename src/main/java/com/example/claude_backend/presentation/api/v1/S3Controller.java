package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.infrastructure.aws.S3Service;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

/**
 * S3 관련 REST API 컨트롤러
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/s3")
@RequiredArgsConstructor
@Tag(name = "S3", description = "S3 파일 관리 API")
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "S3 객체 URL 생성", description = "S3 객체의 직접 접근 URL을 생성합니다.")
    @GetMapping("/object-url")
    public ResponseEntity<ApiResponse<String>> getS3ObjectUrl(@RequestParam String key) {
        log.debug("S3 객체 URL 생성 요청. key: {}", key);

        String url = s3Service.getObjectUrl(key);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    @Operation(summary = "맵 파일 URL 생성", description = "맵 파일의 S3 URL을 생성합니다.")
    @GetMapping("/map-url")
    public ResponseEntity<ApiResponse<String>> getMapUrl(@RequestParam String mapName) {
        log.debug("맵 파일 URL 생성 요청. mapName: {}", mapName);

        String url = s3Service.getMapUrl(mapName);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    @Operation(summary = "캐릭터 파일 URL 생성", description = "캐릭터 파일의 S3 URL을 생성합니다.")
    @GetMapping("/character-url")
    public ResponseEntity<ApiResponse<String>> getCharacterUrl(@RequestParam String characterName) {
        log.debug("캐릭터 파일 URL 생성 요청. characterName: {}", characterName);

        String url = s3Service.getCharacterUrl(characterName);
        return ResponseEntity.ok(ApiResponse.success(url));
    }
}