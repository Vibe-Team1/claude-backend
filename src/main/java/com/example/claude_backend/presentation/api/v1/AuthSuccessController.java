package com.example.claude_backend.presentation.api.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 성공 후 토큰을 표시하는 컨트롤러
 */
@Controller
public class AuthSuccessController {

    @GetMapping("/auth/success")
    @ResponseBody
    public Map<String, Object> authSuccess(
            @RequestParam(name = "token", required = false) String token,      // name 추가
            @RequestParam(name = "refresh", required = false) String refresh)  {

        Map<String, Object> response = new HashMap<>();

        if (token != null && refresh != null) {
            response.put("success", true);
            response.put("message", "로그인 성공! 아래 토큰을 Postman에서 사용하세요.");
            response.put("accessToken", token);
            response.put("refreshToken", refresh);
            response.put("usage", "Authorization: Bearer " + token);
        } else {
            response.put("success", false);
            response.put("message", "토큰이 없습니다. 다시 로그인해주세요.");
        }

        return response;
    }
}