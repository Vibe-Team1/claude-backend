package com.example.claude_backend.application.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 메시지 DTO WebSocket을 통해 주고받는 채팅 메시지 구조
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

  /** 메시지 타입 */
  private MessageType type;

  /** 채팅방 ID */
  private String roomId;

  /** 발신자 ID */
  private UUID senderId;

  /** 발신자 닉네임 */
  private String senderNickname;

  /** 발신자 캐릭터 코드 */
  private String senderCharacterCode;

  /** 메시지 내용 */
  private String content;

  /** 전송 시간 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp;

  /** 메시지 타입 enum */
  public enum MessageType {
    CHAT, // 일반 채팅
    JOIN, // 입장
    LEAVE // 퇴장
  }
}
