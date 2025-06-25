package com.example.claude_backend.application.chat.service;

import com.example.claude_backend.application.chat.dto.ChatMessage;
import com.example.claude_backend.application.chat.dto.ChatRoomInfo;
import com.example.claude_backend.application.user.service.UserService;
import com.example.claude_backend.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

/**
 * 채팅 서비스 구현체 Redis Pub/Sub을 사용한 채팅 기능 구현
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisMessageListenerContainer redisMessageListenerContainer;
  private final UserService userService;

  // 채팅방별 접속자 관리 (메모리 기반, Redis 대신 성능 최적화)
  private final Map<String, Set<UUID>> roomUsers = new ConcurrentHashMap<>();

  // 사용자별 접속 채팅방 관리
  private final Map<UUID, Set<String>> userRooms = new ConcurrentHashMap<>();

  /** 채팅 메시지 전송 */
  @Override
  public ChatMessage sendMessage(ChatMessage message) {
    // 메시지 타임스탬프 설정
    message.setTimestamp(LocalDateTime.now());

    // Redis Pub/Sub으로 메시지 브로드캐스트
    String topic = "chat.room." + message.getRoomId();
    redisTemplate.convertAndSend(topic, message);

    log.debug(
        "채팅 메시지 전송 - 방: {}, 발신자: {}, 내용: {}",
        message.getRoomId(),
        message.getSenderNickname(),
        message.getContent());

    return message;
  }

  /** 채팅방 입장 */
  @Override
  public ChatMessage joinRoom(String roomId, UUID userId) {
    // 사용자 정보 조회
    User user = userService.getUserEntityById(userId);

    // 채팅방 접속자 목록에 추가
    roomUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    userRooms.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(roomId);

    // 입장 메시지 생성
    ChatMessage joinMessage =
        ChatMessage.builder()
            .type(ChatMessage.MessageType.JOIN)
            .roomId(roomId)
            .senderId(userId)
            .senderNickname(user.getNickname())
            .senderCharacterCode(getUserCharacterCode(user))
            .content(user.getNickname() + "님이 입장하셨습니다.")
            .timestamp(LocalDateTime.now())
            .build();

    // Redis Pub/Sub으로 입장 메시지 브로드캐스트
    String topic = "chat.room." + roomId;
    redisTemplate.convertAndSend(topic, joinMessage);

    log.info("채팅방 입장 - 방: {}, 사용자: {}", roomId, user.getNickname());

    return joinMessage;
  }

  /** 채팅방 퇴장 */
  @Override
  public ChatMessage leaveRoom(String roomId, UUID userId) {
    // 사용자 정보 조회
    User user = userService.getUserEntityById(userId);

    // 채팅방 접속자 목록에서 제거
    Set<UUID> users = roomUsers.get(roomId);
    if (users != null) {
      users.remove(userId);
      if (users.isEmpty()) {
        roomUsers.remove(roomId);
      }
    }

    // 사용자 접속 채팅방 목록에서 제거
    Set<String> rooms = userRooms.get(userId);
    if (rooms != null) {
      rooms.remove(roomId);
      if (rooms.isEmpty()) {
        userRooms.remove(userId);
      }
    }

    // 퇴장 메시지 생성
    ChatMessage leaveMessage =
        ChatMessage.builder()
            .type(ChatMessage.MessageType.LEAVE)
            .roomId(roomId)
            .senderId(userId)
            .senderNickname(user.getNickname())
            .senderCharacterCode(getUserCharacterCode(user))
            .content(user.getNickname() + "님이 퇴장하셨습니다.")
            .timestamp(LocalDateTime.now())
            .build();

    // Redis Pub/Sub으로 퇴장 메시지 브로드캐스트
    String topic = "chat.room." + roomId;
    redisTemplate.convertAndSend(topic, leaveMessage);

    log.info("채팅방 퇴장 - 방: {}, 사용자: {}", roomId, user.getNickname());

    return leaveMessage;
  }

  /** 채팅방 정보 조회 */
  @Override
  public ChatRoomInfo getRoomInfo(String roomId) {
    Set<UUID> users = roomUsers.getOrDefault(roomId, Collections.emptySet());

    return ChatRoomInfo.builder()
        .roomId(roomId)
        .roomName("채팅방 " + roomId)
        .currentUserCount(users.size())
        .connectedUsers(new HashSet<>(users))
        .maxUserCount(50) // 최대 50명
        .build();
  }

  /** 사용자가 접속한 채팅방 목록 조회 */
  @Override
  public List<String> getUserRooms(UUID userId) {
    Set<String> rooms = userRooms.getOrDefault(userId, Collections.emptySet());
    return new ArrayList<>(rooms);
  }

  /** 사용자의 현재 캐릭터 코드 조회 */
  private String getUserCharacterCode(User user) {
    if (user.getProfile() != null && user.getProfile().getCurrentCharacterCode() != null) {
      return user.getProfile().getCurrentCharacterCode();
    }
    return "001"; // 기본 캐릭터
  }
}
