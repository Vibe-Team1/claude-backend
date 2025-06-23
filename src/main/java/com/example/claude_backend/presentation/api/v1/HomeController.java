package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping("/")
  public ResponseEntity<ApiResponse<Map<String, Object>>> home() {
    Map<String, Object> data = new HashMap<>();
    data.put("message", "StockRoom SNS API 서버가 정상적으로 실행 중입니다.");
    data.put("status", "ONLINE");
    data.put("timestamp", LocalDateTime.now());
    data.put(
        "endpoints",
        Map.of(
            "swagger", "/swagger-ui.html",
            "auth_error", "/api/v1/auth/error",
            "auth_success", "/auth/success",
            "auth_token", "/auth/token",
            "oauth2_google", "/oauth2/authorization/google"));

    return ResponseEntity.ok(ApiResponse.success(data));
  }
}
