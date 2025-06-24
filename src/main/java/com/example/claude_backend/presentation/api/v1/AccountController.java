package com.example.claude_backend.presentation.api.v1;

import com.example.claude_backend.application.account.dto.AccountResponse;
import com.example.claude_backend.application.account.service.AccountService;
import com.example.claude_backend.common.util.SecurityUtil;
import com.example.claude_backend.presentation.api.v1.response.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  /** 계좌 생성 */
  @PostMapping
  public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
      @RequestParam(defaultValue = "1000000.0") Double initialBalance,
      Authentication authentication) {

    UUID userId = SecurityUtil.getCurrentUserId();
    AccountResponse accountInfo = accountService.createAccountForUser(userId, initialBalance);

    return ResponseEntity.ok(ApiResponse.success(accountInfo));
  }

  /** 계좌 정보 조회 */
  @GetMapping
  public ResponseEntity<ApiResponse<AccountResponse>> getAccountInfo(
      Authentication authentication) {
    UUID userId = SecurityUtil.getCurrentUserId();
    AccountResponse accountInfo = accountService.getAccountInfo(userId);

    return ResponseEntity.ok(ApiResponse.success(accountInfo));
  }

  /** 계좌 입금 */
  @PostMapping("/deposit")
  public ResponseEntity<ApiResponse<String>> deposit(
      @RequestParam Double amount, Authentication authentication) {

    UUID userId = SecurityUtil.getCurrentUserId();
    accountService.deposit(userId, amount);

    return ResponseEntity.ok(ApiResponse.success("입금이 완료되었습니다."));
  }

  /** 계좌 출금 */
  @PostMapping("/withdraw")
  public ResponseEntity<ApiResponse<String>> withdraw(
      @RequestParam Double amount, Authentication authentication) {

    UUID userId = SecurityUtil.getCurrentUserId();
    accountService.withdraw(userId, amount);

    return ResponseEntity.ok(ApiResponse.success("출금이 완료되었습니다."));
  }

  /** 도토리 조회 */
  @GetMapping("/acorn")
  public ResponseEntity<ApiResponse<Integer>> getAcorn(Authentication authentication) {
    UUID userId = SecurityUtil.getCurrentUserId();
    Integer acorn = accountService.getAcorn(userId);

    return ResponseEntity.ok(ApiResponse.success(acorn));
  }

  /** 도토리 증가 (관리자용) */
  @PostMapping("/acorn/add")
  public ResponseEntity<ApiResponse<String>> addAcorn(
      @RequestParam Integer amount, Authentication authentication) {

    UUID userId = SecurityUtil.getCurrentUserId();
    accountService.addAcorn(userId, amount);

    return ResponseEntity.ok(ApiResponse.success("도토리가 증가되었습니다."));
  }

  /** 도토리 감소 (관리자용) */
  @PostMapping("/acorn/subtract")
  public ResponseEntity<ApiResponse<String>> subtractAcorn(
      @RequestParam Integer amount, Authentication authentication) {

    UUID userId = SecurityUtil.getCurrentUserId();
    accountService.subtractAcorn(userId, amount);

    return ResponseEntity.ok(ApiResponse.success("도토리가 감소되었습니다."));
  }
}
