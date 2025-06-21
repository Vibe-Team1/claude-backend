package com.example.claude_backend.infrastructure.persistence.jpa;


import java.util.Optional;
import java.util.UUID;

import com.example.claude_backend.domain.user.repository.UserRepository;
import org.h2.engine.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * UserRepository의 JPA 구현체
 * Spring Data JPA를 사용하여 실제 데이터베이스 연동
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {

    /**
     * 이메일로 사용자 조회 (프로필과 권한 함께 로드)
     */
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.profile " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(@Param("email") String email);

    /**
     * Google Subject ID로 사용자 조회 (프로필과 권한 함께 로드)
     */
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.profile " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE u.googleSub = :googleSub")
    Optional<User> findByGoogleSubWithDetails(@Param("googleSub") String googleSub);

    /**
     * ID로 사용자 조회 (프로필과 권한 함께 로드)
     */
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.profile " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") UUID id);

    /**
     * 닉네임 중복 체크용 쿼리
     *
     * @param nickname 닉네임
     * @param excludeUserId 제외할 사용자 ID (본인 제외)
     * @return 존재 여부
     */
    @Query("SELECT COUNT(u) > 0 FROM User u " +
            "WHERE u.nickname = :nickname " +
            "AND (:excludeUserId IS NULL OR u.id != :excludeUserId)")
    boolean existsByNicknameExcludingUser(@Param("nickname") String nickname,
                                          @Param("excludeUserId") UUID excludeUserId);
}