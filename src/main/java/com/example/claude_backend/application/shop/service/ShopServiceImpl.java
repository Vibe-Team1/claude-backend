package com.example.claude_backend.application.shop.service;

import com.example.claude_backend.application.shop.dto.DrawRequest;
import com.example.claude_backend.application.shop.dto.DrawResponse;
import com.example.claude_backend.domain.account.entity.Account;
import com.example.claude_backend.domain.account.exception.InsufficientBalanceException;
import com.example.claude_backend.domain.account.repository.AccountRepository;
import com.example.claude_backend.domain.shop.exception.InsufficientAcornException;
import com.example.claude_backend.domain.shop.exception.InvalidDrawTypeException;
import com.example.claude_backend.domain.user.entity.UserCharacter;
import com.example.claude_backend.domain.user.repository.UserCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopServiceImpl implements ShopService {

    private final AccountRepository accountRepository;
    private final UserCharacterRepository userCharacterRepository;

    @Override
    @Transactional
    public DrawResponse drawCharacter(UUID userId, DrawRequest request) {
        String drawType = request.getType();
        log.debug("캐릭터 뽑기 요청. userId: {}, type: {}", userId, drawType);

        // 뽑기 타입 유효성 검사
        DrawType type = DrawType.fromString(drawType);
        if (type == null) {
            throw new InvalidDrawTypeException(drawType);
        }

        // 계정 조회
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("계정을 찾을 수 없습니다: " + userId));

        // 도토리 잔액 확인
        if (account.getAcorn() < type.getCost()) {
            throw new InsufficientAcornException(type.getCost(), account.getAcorn());
        }

        // 랜덤 캐릭터 코드 생성
        String characterCode = generateRandomCharacterCode(type);

        // 이미 보유 중인지 확인
        boolean isNew = !userCharacterRepository.existsByUserIdAndCharacterCode(userId, characterCode);

        // 새로운 캐릭터인 경우 저장
        if (isNew) {
            UserCharacter userCharacter = UserCharacter.builder()
                    .userId(userId)
                    .characterCode(characterCode)
                    .build();
            userCharacterRepository.save(userCharacter);
            log.info("새로운 캐릭터 획득. userId: {}, characterCode: {}", userId, characterCode);
        } else {
            log.info("이미 보유 중인 캐릭터. userId: {}, characterCode: {}", userId, characterCode);
        }

        // 도토리 차감
        account.setAcorn(account.getAcorn() - type.getCost());
        accountRepository.save(account);

        log.info("캐릭터 뽑기 완료. userId: {}, type: {}, characterCode: {}, isNew: {}, cost: {}",
                userId, drawType, characterCode, isNew, type.getCost());

        return DrawResponse.builder()
                .characterCode(characterCode)
                .isNew(isNew)
                .build();
    }

    private String generateRandomCharacterCode(DrawType type) {
        int minCode = type.getMinCode();
        int maxCode = type.getMaxCode();
        int randomCode = ThreadLocalRandom.current().nextInt(minCode, maxCode + 1);
        return String.format("%03d", randomCode);
    }

    private enum DrawType {
        NORMAL(5, 1, 120),
        PREMIUM(10, 121, 180),
        SUPREME(20, 150, 180);

        private final int cost;
        private final int minCode;
        private final int maxCode;

        DrawType(int cost, int minCode, int maxCode) {
            this.cost = cost;
            this.minCode = minCode;
            this.maxCode = maxCode;
        }

        public int getCost() {
            return cost;
        }

        public int getMinCode() {
            return minCode;
        }

        public int getMaxCode() {
            return maxCode;
        }

        public static DrawType fromString(String type) {
            try {
                return DrawType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}