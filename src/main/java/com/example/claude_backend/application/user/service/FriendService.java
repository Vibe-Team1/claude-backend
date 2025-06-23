package com.example.claude_backend.application.user.service;

import com.example.claude_backend.application.user.dto.FriendRequest;
import com.example.claude_backend.application.user.dto.SimpleUserResponse;

import java.util.List;
import java.util.UUID;

public interface FriendService {

    /**
     * 전체 사용자 목록 조회 (자기 자신 제외)
     *
     * @param currentUserId 현재 로그인한 사용자 ID
     * @return 전체 사용자 목록 (자기 자신 제외)
     */
    List<SimpleUserResponse> getAllUsers(UUID currentUserId);

    /**
     * 현재 사용자의 친구 목록 조회
     *
     * @param userId 현재 사용자 ID
     * @return 친구 목록
     */
    List<SimpleUserResponse> getFriends(UUID userId);

    /**
     * 친구 추가
     *
     * @param userId  현재 사용자 ID
     * @param request 친구 추가 요청
     */
    void addFriend(UUID userId, FriendRequest request);
}