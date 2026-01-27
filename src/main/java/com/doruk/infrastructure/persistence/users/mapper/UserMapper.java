package com.doruk.infrastructure.persistence.users.mapper;

import com.doruk.application.app.users.dto.CurrentUserDto;
import com.doruk.application.app.users.dto.ProfileDto;
import com.doruk.application.app.users.dto.UserResponseDto;
import com.doruk.infrastructure.persistence.entity.Role;
import com.doruk.infrastructure.persistence.entity.User;
import jakarta.inject.Singleton;


@Singleton
public class UserMapper {
    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .emailVerified(user.emailVerified())
                .phoneVerified(user.phoneVerified())
                .phone(user.phone())
                .status(user.status())
                .multiFactorAuth(user.multiFactorAuth())
                .build();
    }

    public CurrentUserDto toCurrentUserDto(User user) {
        var pr = user.profile();
        return CurrentUserDto.builder()
                .username(user.username())
                .email(user.email())
                .phone(user.phone())
                .id(user.id().toString())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .emailVerified(user.emailVerified())
                .multiFactorAuth(user.multiFactorAuth())
                .phoneVerified(user.phoneVerified())
                .status(user.status())
                .profile(ProfileDto.builder()
                        .city(pr.city())
                        .address(pr.address())
                        .country(pr.country())
                        .createdAt(pr.createdAt())
                        .updatedAt(pr.updatedAt())
                        .fullName(pr.fullName())
                        .postalCode(pr.postalCode())
                        .state(pr.state())
                        .build())
                .roles(user.roles().stream().map(Role::name).toList())
                .build();
    }
}
