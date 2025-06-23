package com.example.claude_backend.application.account.service;

import com.example.claude_backend.application.account.dto.AccountResponse;
import com.example.claude_backend.domain.account.entity.Account;
import com.example.claude_backend.domain.account.exception.InsufficientBalanceException;
import com.example.claude_backend.domain.account.repository.AccountRepository;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 계좌 서비스
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  /** 잔액 차감 */
  @Transactional
  public void deductBalance(UUID accountId, BigDecimal amount) {
    Account account = accountRepository
        .findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다: " + accountId));

    account.subtractBalance(amount);
    accountRepository.save(account);
    log.info("계좌 잔액 차감 완료 - 계좌 ID: {}, 차감 금액: {}", accountId, amount);
  }

  /** 잔액 증가 */
  @Transactional
  public void addBalance(UUID accountId, BigDecimal amount) {
    Account account = accountRepository
        .findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다: " + accountId));

    account.addBalance(amount);
    accountRepository.save(account);
    log.info("계좌 잔액 증가 완료 - 계좌 ID: {}, 증가 금액: {}", accountId, amount);
  }

  /** 사용자 ID로 계좌 조회 */
  public Account getAccountByUserId(UUID userId) {
    return accountRepository
        .findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자 계좌를 찾을 수 없습니다: " + userId));
  }

  /** 계좌 생성 */
  @Transactional
  public Account createAccount(User user, BigDecimal initialBalance) {
    Account account = Account.builder().user(user).balance(initialBalance).build();

    Account savedAccount = accountRepository.save(account);
    log.info("계좌 생성 완료 - 사용자 ID: {}, 초기 잔액: {}", user.getId(), initialBalance);
    return savedAccount;
  }

  /** 잔액 조회 */
  public BigDecimal getBalance(UUID accountId) {
    Account account = accountRepository
        .findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다: " + accountId));
    return account.getBalance();
  }

  /** 잔액 충분 여부 확인 */
  public boolean hasSufficientBalance(UUID accountId, BigDecimal amount) {
    Account account = accountRepository
        .findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다: " + accountId));
    return account.hasSufficientBalance(amount);
  }

  /** 사용자 계좌 정보 조회 (UUID userId 사용) */
  public Account getUserAccount(UUID userId) {
    return accountRepository
        .findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자 계좌를 찾을 수 없습니다: " + userId));
  }

  /** 사용자 계좌 정보 조회 (DTO 반환) */
  public AccountResponse getAccountInfo(UUID userId) {
    Account account = getUserAccount(userId);
    return convertToAccountResponse(account);
  }

  /** 사용자 계좌 생성 */
  @Transactional
  public AccountResponse createAccountForUser(UUID userId, Double initialBalance) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

    // 이미 계좌가 있는지 확인
    if (accountRepository.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException("이미 계좌가 존재합니다: " + userId);
    }

    Account account = createAccount(user, BigDecimal.valueOf(initialBalance));
    return convertToAccountResponse(account);
  }

  /** 계좌 입금 */
  @Transactional
  public void deposit(UUID userId, Double amount) {
    Account account = getUserAccount(userId);
    account.addBalance(BigDecimal.valueOf(amount));
    accountRepository.save(account);
    log.info("계좌 입금 완료 - 사용자 ID: {}, 입금 금액: {}", userId, amount);
  }

  /** 계좌 출금 */
  @Transactional
  public void withdraw(UUID userId, Double amount) {
    Account account = getUserAccount(userId);
    if (!account.hasSufficientBalance(BigDecimal.valueOf(amount))) {
      throw new InsufficientBalanceException("잔액이 부족합니다.");
    }
    account.subtractBalance(BigDecimal.valueOf(amount));
    accountRepository.save(account);
    log.info("계좌 출금 완료 - 사용자 ID: {}, 출금 금액: {}", userId, amount);
  }

  /** 도토리 조회 */
  public Integer getAcorn(UUID userId) {
    Account account = getUserAccount(userId);
    return account.getAcorn();
  }

  /** 도토리 증가 */
  @Transactional
  public void addAcorn(UUID userId, Integer amount) {
    Account account = getUserAccount(userId);
    account.addAcorn(amount);
    accountRepository.save(account);
    log.info("도토리 증가 완료 - 사용자 ID: {}, 증가 수량: {}", userId, amount);
  }

  /** 도토리 감소 */
  @Transactional
  public void subtractAcorn(UUID userId, Integer amount) {
    Account account = getUserAccount(userId);
    if (!account.hasSufficientAcorn(amount)) {
      throw new InsufficientBalanceException("도토리가 부족합니다.");
    }
    account.subtractAcorn(amount);
    accountRepository.save(account);
    log.info("도토리 감소 완료 - 사용자 ID: {}, 감소 수량: {}", userId, amount);
  }

  /** 도토리 충분 여부 확인 */
  public boolean hasSufficientAcorn(UUID userId, Integer amount) {
    Account account = getUserAccount(userId);
    return account.hasSufficientAcorn(amount);
  }

  /** Account를 AccountResponse로 변환 */
  private AccountResponse convertToAccountResponse(Account account) {
    return AccountResponse.builder()
        .accountId(account.getId())
        .userId(account.getUser().getId())
        .balance(account.getBalance())
        .acorn(account.getAcorn())
        .createdAt(account.getCreatedAt())
        .updatedAt(account.getUpdatedAt())
        .build();
  }
}
