package com.example.claude_backend.infrastructure.persistence.jpa;

import com.example.claude_backend.application.user.dto.UserSearchResponse;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * UserRepository의 JPA 구현체 Spring Data JPA를 사용하여 실제 데이터베이스 연동
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {

    /** 이메일로 사용자 조회 (프로필과 권한 함께 로드) */
    @Query("SELECT u FROM User u "
            + "LEFT JOIN FETCH u.profile "
            + "LEFT JOIN FETCH u.roles "
            + "WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(@Param("email") String email);

    /** Google Subject ID로 사용자 조회 (프로필과 권한 함께 로드) */
    @Query("SELECT u FROM User u "
            + "LEFT JOIN FETCH u.profile "
            + "LEFT JOIN FETCH u.roles "
            + "WHERE u.googleSub = :googleSub")
    Optional<User> findByGoogleSubWithDetails(@Param("googleSub") String googleSub);

    /** ID로 사용자 조회 (프로필과 권한 함께 로드) */
    @Query("SELECT u FROM User u "
            + "LEFT JOIN FETCH u.profile "
            + "LEFT JOIN FETCH u.roles "
            + "WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") UUID id);

    /** ID로 사용자 조회 (Long 타입) */
    @Override
    default Optional<User> findById(Long id) {
        // Long ID를 UUID로 변환하는 로직이 필요하지만,
        // 현재는 간단히 모든 사용자를 조회하여 해시값으로 매칭
        return findAll().stream().filter(user -> Math.abs(user.getId().hashCode()) == id).findFirst();
    }

    /**
     * 닉네임 중복 체크용 쿼리
     *
     * @param nickname      닉네임
     * @param excludeUserId 제외할 사용자 ID (본인 제외)
     * @return 존재 여부
     */
    @Query("SELECT COUNT(u) > 0 FROM User u "
            + "WHERE u.nickname = :nickname "
            + "AND (:excludeUserId IS NULL OR u.id != :excludeUserId)")
    boolean existsByNicknameExcludingUser(
            @Param("nickname") String nickname, @Param("excludeUserId") UUID excludeUserId);

    /** 닉네임으로 사용자 검색 (부분 일치) */
    @Override
    List<User> findByNicknameContainingIgnoreCase(String nickname);

    /** 사용자 검색을 위한 최적화된 쿼리 닉네임, 현재 캐릭터 코드, 계좌 잔액, 보유 캐릭터 개수를 한 번에 조회 */
    @Query("SELECT new com.example.claude_backend.application.user.dto.UserSearchResponse("
            + "CAST(u.id AS string), "
            + "u.nickname, "
            + "up.currentCharacterCode, "
            + "CAST(a.balance AS long), "
            + "CAST(COUNT(uc.id) AS integer)) "
            + "FROM User u "
            + "LEFT JOIN u.profile up "
            + "LEFT JOIN u.account a "
            + "LEFT JOIN u.userCharacters uc "
            + "WHERE LOWER(u.nickname) LIKE LOWER(CONCAT('%', :nickname, '%')) "
            + "GROUP BY u.id, u.nickname, up.currentCharacterCode, a.balance")
    List<UserSearchResponse> searchUsersByNickname(@Param("nickname") String nickname);
}
