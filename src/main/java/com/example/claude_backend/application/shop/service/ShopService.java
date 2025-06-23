package com.example.claude_backend.application.shop.service;

import com.example.claude_backend.application.shop.dto.DrawRequest;
import com.example.claude_backend.application.shop.dto.DrawResponse;

import java.util.UUID;

public interface ShopService {

    /**
     * 캐릭터 뽑기
     *
     * @param userId  현재 사용자 ID
     * @param request 뽑기 요청
     * @return 뽑기 결과
     */
    DrawResponse drawCharacter(UUID userId, DrawRequest request);
}