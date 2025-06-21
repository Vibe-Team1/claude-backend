package com.example.claude_backend.domain.user.repository;

import org.h2.engine.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 Repository 인터페이스
 * Clean Architecture의 Domain 계층에 위치
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
public interface UserRepository {

    /**
     * 사용자 저장
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);


    /**
     * ID로 사용자 조회
     *
     * @param id 사용자 ID
     * @return 사용자 Optional
     */
    Optional<User> findById(UUID id);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Google Subject ID로 사용자 조회
     *
     * @param googleSub Google OAuth Subject ID
     * @return 사용자 Optional
     */
    Optional<User> findByGoogleSub(String googleSub);

    /**
     * 닉네임으로 사용자 조회
     *
     * @param nickname 닉네임
     * @return 사용자 Optional
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 존재 여부 확인
     *
     * @param nickname 닉네임
     * @return 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 사용자 삭제
     *
     * @param user 삭제할 사용자
     */
    void delete(User user);
}