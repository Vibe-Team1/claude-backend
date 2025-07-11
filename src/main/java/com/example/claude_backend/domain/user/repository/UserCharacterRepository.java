package com.example.claude_backend.domain.user.repository;

import com.example.claude_backend.domain.user.entity.UserCharacter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {

  @Query("SELECT uc FROM UserCharacter uc WHERE uc.user.id = :userId")
  List<UserCharacter> findByUserId(@Param("userId") UUID userId);

  @Query("SELECT uc.characterCode FROM UserCharacter uc WHERE uc.user.id = :userId")
  List<String> findCharacterCodesByUserId(@Param("userId") UUID userId);

  @Query(
      "SELECT uc FROM UserCharacter uc WHERE uc.user.id = :userId AND uc.characterCode = :characterCode")
  Optional<UserCharacter> findByUserIdAndCharacterCode(
      @Param("userId") UUID userId, @Param("characterCode") String characterCode);

  @Query(
      "SELECT COUNT(uc) > 0 FROM UserCharacter uc WHERE uc.user.id = :userId AND uc.characterCode = :characterCode")
  boolean existsByUserIdAndCharacterCode(
      @Param("userId") UUID userId, @Param("characterCode") String characterCode);
}
