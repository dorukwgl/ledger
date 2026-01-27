package com.doruk.infrastructure.persistence.auth.mapper;

import com.doruk.application.app.auth.dto.SessionDto;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.persistence.entity.Session;
import jakarta.inject.Singleton;

import java.util.stream.Collectors;

@Singleton
public class SessionMapper {
    public SessionDto toDto(Session entity) {
        return SessionDto.builder()
                .id(String.valueOf(entity.id()))
                .userId(entity.user().id().toString())
                .sessionId(entity.sessionId())
                .deviceId(entity.deviceId())
                .deviceInfo(entity.deviceInfo())
                .expiresAt(entity.expiresAt())
                .permissions(entity.cachedPermissions()
                        .stream()
                        .map(Permissions::fromId)
                        .collect(Collectors.toSet()))
                .build();
    }
}
