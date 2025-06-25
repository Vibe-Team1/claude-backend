package com.example.claude_backend.application.chat.service;

import com.example.claude_backend.application.chat.dto.ChatMessage;
import com.example.claude_backend.application.chat.dto.ChatRoomInfo;
import java.util.List;
import java.util.UUID;

/**
 * 채팅 서비스 인터페이스 채팅 관련 비즈니스 로직 처리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public interface ChatService {

  /**
   * 채팅 메시지 전송
   *
   * @param message 전송할 메시지
   * @return 전송된 메시지
   */
  ChatMessage sendMessage(ChatMessage message);

  /**
   * 채팅방 입장
   *
   * @param roomId 채팅방 ID
   * @param userId 사용자 ID
   * @return 입장 메시지
   */
  ChatMessage joinRoom(String roomId, UUID userId);

  /**
   * 채팅방 퇴장
   *
   * @param roomId 채팅방 ID
   * @param userId 사용자 ID
   * @return 퇴장 메시지
   */
  ChatMessage leaveRoom(String roomId, UUID userId);

  /**
   * 채팅방 정보 조회
   *
   * @param roomId 채팅방 ID
   * @return 채팅방 정보
   */
  ChatRoomInfo getRoomInfo(String roomId);

  /**
   * 사용자가 접속한 채팅방 목록 조회
   *
   * @param userId 사용자 ID
   * @return 채팅방 목록
   */
  List<String> getUserRooms(UUID userId);
}
