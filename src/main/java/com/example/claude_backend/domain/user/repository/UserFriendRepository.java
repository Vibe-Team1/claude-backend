package com.example.claude_backend.domain.user.repository;

import com.example.claude_backend.domain.user.entity.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {

    @Query("SELECT uf FROM UserFriend uf WHERE uf.userId = :userId")
    List<UserFriend> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT uf.friendId FROM UserFriend uf WHERE uf.userId = :userId")
    List<UUID> findFriendIdsByUserId(@Param("userId") UUID userId);

    @Query("SELECT uf FROM UserFriend uf WHERE uf.userId = :userId AND uf.friendId = :friendId")
    Optional<UserFriend> findByUserIdAndFriendId(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    @Query("SELECT COUNT(uf) > 0 FROM UserFriend uf WHERE uf.userId = :userId AND uf.friendId = :friendId")
    boolean existsByUserIdAndFriendId(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    @Query("DELETE FROM UserFriend uf WHERE uf.userId = :userId AND uf.friendId = :friendId")
    void deleteByUserIdAndFriendId(@Param("userId") UUID userId, @Param("friendId") UUID friendId);
}