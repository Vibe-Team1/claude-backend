package com.example.claude_backend.application.user.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.claude_backend.application.user.dto.UserProfileResponse;
import com.example.claude_backend.application.user.dto.UserResponse;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.entity.UserProfile;
import com.example.claude_backend.domain.user.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * User Entity와 DTO 간의 변환을 담당하는 MapStruct Mapper
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * User Entity를 UserResponse DTO로 변환
     *
     * @param user User 엔티티
     * @return UserResponse DTO
     */
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStringSet")
    @Mapping(source = "profile", target = "profile")
    UserResponse toUserResponse(User user);

    /**
     * UserProfile Entity를 UserProfileResponse DTO로 변환
     *
     * @param profile UserProfile 엔티티
     * @return UserProfileResponse DTO
     */
    UserProfileResponse toUserProfileResponse(UserProfile profile);

    /**
     * User Entity를 간단한 UserResponse DTO로 변환
     *
     * @param user User 엔티티
     * @return 간단한 UserResponse DTO
     */
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserResponse toSimpleUserResponse(User user);

    /**
     * UserStatus enum을 String으로 변환
     */
    @Named("statusToString")
    default String statusToString(User.UserStatus status) {
        return status != null ? status.name() : null;
    }

    /**
     * UserRole Set을 String Set으로 변환
     */
    @Named("rolesToStringSet")
    default Set<String> rolesToStringSet(Set<UserRole> roles) {
        return roles != null ?
                roles.stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toSet()) :
                Set.of();
    }
}