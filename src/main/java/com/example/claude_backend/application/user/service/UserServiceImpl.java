package com.example.claude_backend.application.user.service;

import com.example.claude_backend.application.account.service.AccountService;
import com.example.claude_backend.application.user.dto.UserResponse;
import com.example.claude_backend.application.user.dto.UserStockResponse;
import com.example.claude_backend.application.user.dto.UserUpdateRequest;
import com.example.claude_backend.application.user.mapper.UserMapper;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.entity.UserProfile;
import com.example.claude_backend.domain.user.entity.UserRole;
import com.example.claude_backend.domain.user.entity.UserStock;
import com.example.claude_backend.domain.user.exception.UserNotFoundException;
import com.example.claude_backend.domain.user.repository.UserRepository;
import com.example.claude_backend.domain.user.repository.UserStockRepository;
import com.example.claude_backend.domain.user.repository.UserBackgroundRepository;
import com.example.claude_backend.domain.user.repository.UserCharacterRepository;
import com.example.claude_backend.domain.user.entity.UserBackground;
import com.example.claude_backend.domain.user.entity.UserCharacter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 구현체
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserStockRepository userStockRepository;
  private final UserMapper userMapper;
  private final AccountService accountService;
  private final UserBackgroundRepository userBackgroundRepository;
  private final UserCharacterRepository userCharacterRepository;

  /** 사용자 ID로 조회 */
  @Override
  public UserResponse getUserById(UUID userId) {
    log.debug("사용자 조회 시작. ID: {}", userId);
    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return userMapper.toUserResponse(user);
  }

  /** 이메일로 사용자 조회 */
  @Override
  public UserResponse getUserByEmail(String email) {
    log.debug("사용자 조회 시작. 이메일: {}", email);
    User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

    return userMapper.toUserResponse(user);
  }

  /** 이메일로 사용자 Entity 조회 (내부 사용) */
  @Override
  public User findByEmail(String email) {
    log.debug("사용자 Entity 조회 시작. 이메일: {}", email);
    return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
  }

  /** 사용자 정보 수정 */
  @Override
  @Transactional
  public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
    log.info("사용자 정보 수정 시작. ID: {}", userId);

    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    // 닉네임 변경
    if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
      // 닉네임 중복 체크
      if (!isNicknameAvailable(request.getNickname(), userId)) {
        throw new IllegalArgumentException("이미 사용중인 닉네임입니다: " + request.getNickname());
      }
      user.updateNickname(request.getNickname());
      log.info("닉네임 변경됨. {} -> {}", user.getNickname(), request.getNickname());
    }

    // 프로필 수정
    if (user.getProfile() != null
        && (request.getProfileImageUrl() != null || request.getBio() != null)) {
      user.getProfile().updateProfile(request.getProfileImageUrl(), request.getBio());
      log.info("프로필 정보 수정됨");
    }

    User updatedUser = userRepository.save(user);
    return userMapper.toUserResponse(updatedUser);
  }

  /** 닉네임 중복 확인 */
  @Override
  public boolean isNicknameAvailable(String nickname, UUID excludeUserId) {
    return !userRepository.existsByNickname(nickname)
        || (excludeUserId != null
            && userRepository
                .findByNickname(nickname)
                .map(user -> user.getId().equals(excludeUserId))
                .orElse(false));
  }

  /** OAuth 로그인 처리 */
  @Override
  @Transactional // 이미 있는지 확인
  public User processOAuthLogin(
      String email, String googleSub, String name, String profileImageUrl) {
    log.info("OAuth 로그인 처리 시작. 이메일: {}", email);

    // 디버깅용 로그 추가
    Optional<User> existingUser = userRepository.findByGoogleSub(googleSub);
    log.info("기존 사용자 조회 결과: {}", existingUser.isPresent());

    return existingUser
        .map(
            user -> {
              log.info("기존 사용자 로그인. ID: {}", user.getId());
              if (user.getProfile() != null && profileImageUrl != null) {
                user.getProfile().updateProfile(profileImageUrl, null);
              }
              return userRepository.save(user);
            })
        .orElseGet(
            () -> {
              log.info("신규 사용자 생성 시작");
              User newUser = createNewUser(email, googleSub, name, profileImageUrl);
              log.info("신규 사용자 생성 완료. ID: {}", newUser.getId());
              return newUser;
            });
  }

  @Override
  public User getUserEntityById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }

  /** 사용자 보유 주식 조회 */
  @Override
  public List<UserStockResponse> getUserStocks(UUID userId) {
    log.debug("사용자 보유 주식 조회 시작. ID: {}", userId);

    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

    return userStockRepository.findByUser(user).stream()
        .map(this::convertToUserStockResponse)
        .collect(Collectors.toList());
  }

  /** 신규 사용자 생성 */
  private User createNewUser(String email, String googleSub, String name, String profileImageUrl) {
    // 닉네임 생성 (이메일 앞부분 또는 이름 사용)
    String nickname = generateUniqueNickname(name != null ? name : email.split("@")[0]);

    // 사용자 생성
    User newUser = User.builder()
        .googleSub(googleSub)
        .email(email)
        .nickname(nickname)
        .status(User.UserStatus.ACTIVE)
        .build();

    // 프로필 생성
    UserProfile profile = UserProfile.builder().user(newUser).profileImageUrl(profileImageUrl).build();
    newUser.setProfile(profile);

    // 기본 권한 부여
    UserRole userRole = UserRole.builder().user(newUser).roleName(UserRole.RoleName.ROLE_USER).build();
    newUser.addRole(userRole);

    User savedUser = userRepository.save(newUser);
    log.info("DB에 사용자 저장 완료. ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());

    // 계좌 자동 생성 (초기 잔액: 1,000,000원)
    try {
      accountService.createAccount(savedUser, BigDecimal.valueOf(1000000.0));
      log.info("사용자 계좌 자동 생성 완료. ID: {}", savedUser.getId());
    } catch (Exception e) {
      log.error("사용자 계좌 생성 실패. ID: {}, Error: {}", savedUser.getId(), e.getMessage());
      // 계좌 생성 실패해도 사용자 생성은 계속 진행
    }

    // === 배경/캐릭터 기본 지급 ===
    try {
      if (!userBackgroundRepository.existsByUserIdAndBackgroundCode(savedUser.getId(), "01")) {
        userBackgroundRepository.save(UserBackground.builder()
            .userId(savedUser.getId())
            .backgroundCode("01")
            .build());
        log.info("기본 배경(01) 지급 완료. ID: {}", savedUser.getId());
      }
      if (!userCharacterRepository.existsByUserIdAndCharacterCode(savedUser.getId(), "001")) {
        userCharacterRepository.save(UserCharacter.builder()
            .userId(savedUser.getId())
            .characterCode("001")
            .build());
        log.info("기본 캐릭터(001) 지급 완료. ID: {}", savedUser.getId());
      }
    } catch (Exception e) {
      log.error("기본 배경/캐릭터 지급 실패. ID: {}, Error: {}", savedUser.getId(), e.getMessage());
    }
    // =========================

    return savedUser;
  }

  /** 유니크한 닉네임 생성 */
  private String generateUniqueNickname(String baseName) {
    // 특수문자 제거 및 20자 제한
    String cleanName = baseName.replaceAll("[^가-힣a-zA-Z0-9]", "");
    if (cleanName.length() > 17) {
      cleanName = cleanName.substring(0, 17);
    }

    // 중복 확인 및 번호 추가
    String nickname = cleanName;
    int suffix = 1;
    while (userRepository.existsByNickname(nickname)) {
      nickname = cleanName + suffix++;
      if (nickname.length() > 20) {
        // 너무 길면 기본값 사용
        nickname = "user" + System.currentTimeMillis() % 100000;
      }
    }

    return nickname;
  }

  /** UserStock을 UserStockResponse로 변환 */
  private UserStockResponse convertToUserStockResponse(UserStock userStock) {
    Double currentPrice = userStock.getStock().getCurrentPrice().doubleValue();
    Double totalValue = currentPrice * userStock.getQuantity();
    Double averagePrice = userStock.getAveragePrice() != null
        ? userStock.getAveragePrice().doubleValue()
        : currentPrice;
    Double profitLoss = totalValue - (averagePrice * userStock.getQuantity());
    Double profitLossRate = averagePrice > 0 ? (profitLoss / (averagePrice * userStock.getQuantity())) * 100 : 0.0;

    return UserStockResponse.builder()
        .stockCode(userStock.getStock().getTicker())
        .stockName(userStock.getStock().getName())
        .quantity(userStock.getQuantity().intValue())
        .averagePrice(averagePrice)
        .currentPrice(currentPrice)
        .totalValue(totalValue)
        .profitLoss(profitLoss)
        .profitLossRate(profitLossRate)
        .build();
  }
}
