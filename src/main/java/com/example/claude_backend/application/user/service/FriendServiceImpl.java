package com.example.claude_backend.application.user.service;

import com.example.claude_backend.application.user.dto.FriendRequest;
import com.example.claude_backend.application.user.dto.SimpleUserResponse;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.entity.UserFriend;
import com.example.claude_backend.domain.user.exception.CannotAddSelfAsFriendException;
import com.example.claude_backend.domain.user.exception.FriendAlreadyExistsException;
import com.example.claude_backend.domain.user.exception.UserNotFoundException;
import com.example.claude_backend.domain.user.repository.UserFriendRepository;
import com.example.claude_backend.infrastructure.persistence.jpa.JpaUserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

  private final JpaUserRepository userRepository;
  private final UserFriendRepository userFriendRepository;

  @Override
  public List<SimpleUserResponse> getAllUsers(UUID currentUserId) {
    log.debug("전체 사용자 목록 조회 (자기 자신 제외). currentUserId: {}", currentUserId);

    List<User> users = userRepository.findAll();
    return users.stream()
        .filter(user -> !user.getId().equals(currentUserId)) // 자기 자신 제외
        .map(
            user ->
                SimpleUserResponse.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  public List<SimpleUserResponse> getFriends(UUID userId) {
    log.debug("사용자 친구 목록 조회. userId: {}", userId);

    // 사용자 존재 확인
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }

    // 친구 ID 목록 조회
    List<UUID> friendIds = userFriendRepository.findFriendIdsByUserId(userId);

    // 친구 정보 조회
    List<User> friends = userRepository.findAllById(friendIds);

    return friends.stream()
        .map(
            user ->
                SimpleUserResponse.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void addFriend(UUID userId, FriendRequest request) {
    UUID friendId = request.getFriendId();
    log.debug("친구 추가 요청. userId: {}, friendId: {}", userId, friendId);

    // 자신을 친구로 추가하려는 경우
    if (userId.equals(friendId)) {
      throw new CannotAddSelfAsFriendException();
    }

    // 친구로 추가할 사용자가 존재하는지 확인
    if (!userRepository.existsById(friendId)) {
      throw new UserNotFoundException(friendId);
    }

    // 이미 친구 관계가 존재하는지 확인
    if (userFriendRepository.existsByUserIdAndFriendId(userId, friendId)) {
      throw new FriendAlreadyExistsException(userId, friendId);
    }

    // 양방향 친구 관계 생성
    UserFriend userFriend1 = UserFriend.builder().userId(userId).friendId(friendId).build();

    UserFriend userFriend2 = UserFriend.builder().userId(friendId).friendId(userId).build();

    userFriendRepository.save(userFriend1);
    userFriendRepository.save(userFriend2);

    log.info("친구 관계 생성 완료. userId: {}, friendId: {}", userId, friendId);
  }
}
