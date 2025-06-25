package com.example.claude_backend.application.chat.dto;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅방 정보 DTO 채팅방의 현재 상태 정보
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomInfo {

  /** 채팅방 ID */
  private String roomId;

  /** 채팅방 이름 */
  private String roomName;

  /** 현재 접속자 수 */
  private int currentUserCount;

  /** 접속자 목록 */
  private Set<UUID> connectedUsers;

  /** 최대 접속자 수 */
  private int maxUserCount;
}
