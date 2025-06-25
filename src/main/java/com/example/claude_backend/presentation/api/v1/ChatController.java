package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.chat.dto.ChatMessage;
import com.example.claude_backend.application.chat.dto.ChatRoomInfo;
import com.example.claude_backend.application.chat.service.ChatService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 채팅 컨트롤러 WebSocket STOMP 메시지 처리 및 REST API 제공
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 API")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

  private final ChatService chatService;
  private final SimpMessagingTemplate messagingTemplate;

  /** 채팅 메시지 전송 (WebSocket STOMP) 클라이언트에서 /app/chat.send로 메시지를 보내면 호출됨 */
  @MessageMapping("/chat.send")
  public void sendMessage(
      @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    // WebSocket 세션에서 사용자 ID 추출
    UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
    if (userId == null) {
      log.warn("인증되지 않은 사용자의 메시지 전송 시도");
      return;
    }

    // 발신자 정보 설정
    chatMessage.setSenderId(userId);

    // 메시지 전송
    ChatMessage sentMessage = chatService.sendMessage(chatMessage);

    // 해당 채팅방의 모든 구독자에게 메시지 브로드캐스트
    messagingTemplate.convertAndSend("/topic/room." + chatMessage.getRoomId(), sentMessage);

    log.debug(
        "채팅 메시지 전송 완료 - 방: {}, 발신자: {}", chatMessage.getRoomId(), chatMessage.getSenderNickname());
  }

  /** 채팅방 입장 (WebSocket STOMP) 클라이언트에서 /app/chat.join으로 메시지를 보내면 호출됨 */
  @MessageMapping("/chat.join")
  public void joinRoom(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    // WebSocket 세션에서 사용자 ID 추출
    UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
    if (userId == null) {
      log.warn("인증되지 않은 사용자의 채팅방 입장 시도");
      return;
    }

    // 채팅방 입장 처리
    ChatMessage joinMessage = chatService.joinRoom(chatMessage.getRoomId(), userId);

    // 해당 채팅방의 모든 구독자에게 입장 메시지 브로드캐스트
    messagingTemplate.convertAndSend("/topic/room." + chatMessage.getRoomId(), joinMessage);

    log.info(
        "채팅방 입장 완료 - 방: {}, 사용자: {}", chatMessage.getRoomId(), joinMessage.getSenderNickname());
  }

  /** 채팅방 퇴장 (WebSocket STOMP) 클라이언트에서 /app/chat.leave로 메시지를 보내면 호출됨 */
  @MessageMapping("/chat.leave")
  public void leaveRoom(
      @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    // WebSocket 세션에서 사용자 ID 추출
    UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
    if (userId == null) {
      log.warn("인증되지 않은 사용자의 채팅방 퇴장 시도");
      return;
    }

    // 채팅방 퇴장 처리
    ChatMessage leaveMessage = chatService.leaveRoom(chatMessage.getRoomId(), userId);

    // 해당 채팅방의 모든 구독자에게 퇴장 메시지 브로드캐스트
    messagingTemplate.convertAndSend("/topic/room." + chatMessage.getRoomId(), leaveMessage);

    log.info(
        "채팅방 퇴장 완료 - 방: {}, 사용자: {}", chatMessage.getRoomId(), leaveMessage.getSenderNickname());
  }

  /** 채팅방 정보 조회 (REST API) */
  @GetMapping("/api/v1/chat/rooms/{roomId}")
  @ResponseBody
  @Operation(summary = "채팅방 정보 조회", description = "특정 채팅방의 정보를 조회합니다.")
  public ApiResponse<ChatRoomInfo> getRoomInfo(@PathVariable String roomId) {
    log.debug("채팅방 정보 조회 요청 - 방: {}", roomId);

    ChatRoomInfo roomInfo = chatService.getRoomInfo(roomId);
    return ApiResponse.success(roomInfo);
  }

  /** 사용자의 접속 채팅방 목록 조회 (REST API) */
  @GetMapping("/api/v1/chat/rooms")
  @ResponseBody
  @Operation(summary = "내 채팅방 목록 조회", description = "현재 사용자가 접속한 채팅방 목록을 조회합니다.")
  public ApiResponse<List<String>> getUserRooms() {
    UUID userId = SecurityUtil.getCurrentUserId();
    log.debug("사용자 채팅방 목록 조회 요청 - 사용자: {}", userId);

    List<String> userRooms = chatService.getUserRooms(userId);
    return ApiResponse.success(userRooms);
  }
}
