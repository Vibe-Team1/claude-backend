package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthErrorController {

  @GetMapping("/error")
  public ResponseEntity<ApiResponse<String>> authError() {
    return ResponseEntity.status(401)
        .body(ApiResponse.error("AUTH_ERROR", "인증에 실패했습니다. 다시 로그인 해주세요."));
  }
}
