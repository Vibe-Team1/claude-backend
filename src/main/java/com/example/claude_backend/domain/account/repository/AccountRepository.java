package com.example.claude_backend.domain.account.repository;

import com.example.claude_backend.domain.account.entity.Account;
import com.example.claude_backend.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 계좌 리포지토리
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

  /** 사용자로 계좌 조회 */
  Optional<Account> findByUser(User user);

  /** 사용자 ID로 계좌 조회 */
  Optional<Account> findByUserId(UUID userId);
}
