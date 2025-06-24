package com.example.claude_backend.domain.user.repository;

import com.example.claude_backend.domain.user.entity.UserBackground;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBackgroundRepository extends JpaRepository<UserBackground, Long> {

  @Query("SELECT ub FROM UserBackground ub WHERE ub.userId = :userId")
  List<UserBackground> findByUserId(@Param("userId") UUID userId);

  @Query("SELECT ub.backgroundCode FROM UserBackground ub WHERE ub.userId = :userId")
  List<String> findBackgroundCodesByUserId(@Param("userId") UUID userId);

  @Query(
      "SELECT ub FROM UserBackground ub WHERE ub.userId = :userId AND ub.backgroundCode = :backgroundCode")
  Optional<UserBackground> findByUserIdAndBackgroundCode(
      @Param("userId") UUID userId, @Param("backgroundCode") String backgroundCode);

  @Query(
      "SELECT COUNT(ub) > 0 FROM UserBackground ub WHERE ub.userId = :userId AND ub.backgroundCode = :backgroundCode")
  boolean existsByUserIdAndBackgroundCode(
      @Param("userId") UUID userId, @Param("backgroundCode") String backgroundCode);
}
