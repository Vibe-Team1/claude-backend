package com.example.claude_backend.application.user.mapper;

import com.example.claude_backend.application.user.dto.UserProfileResponse;
import com.example.claude_backend.application.user.dto.UserResponse;
import com.example.claude_backend.domain.user.entity.User;
import com.example.claude_backend.domain.user.entity.UserProfile;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2025-06-22T02:53:09+0900", comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)")
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.status(statusToString(user.getStatus()));
        userResponse.roles(rolesToStringSet(user.getRoles()));
        userResponse.profile(toUserProfileResponse(user.getProfile()));
        userResponse.id(user.getId());
        userResponse.email(user.getEmail());
        userResponse.nickname(user.getNickname());
        userResponse.createdAt(user.getCreatedAt());
        userResponse.updatedAt(user.getUpdatedAt());

        return userResponse.build();
    }

    @Override
    public UserProfileResponse toUserProfileResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }

        UserProfileResponse.UserProfileResponseBuilder userProfileResponse = UserProfileResponse.builder();

        userProfileResponse.profileImageUrl(profile.getProfileImageUrl());
        userProfileResponse.bio(profile.getBio());
        userProfileResponse.totalAssets(profile.getTotalAssets());
        userProfileResponse.roomLevel(profile.getRoomLevel());
        userProfileResponse.currentBackgroundCode(profile.getCurrentBackgroundCode());
        userProfileResponse.currentCharacterCode(profile.getCurrentCharacterCode());

        return userProfileResponse.build();
    }

    @Override
    public UserResponse toSimpleUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id(user.getId());
        userResponse.nickname(user.getNickname());

        return userResponse.build();
    }
}
