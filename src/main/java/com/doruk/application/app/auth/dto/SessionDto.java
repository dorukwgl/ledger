package com.doruk.application.app.auth.dto;

import com.doruk.domain.shared.enums.Permissions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Serdeable
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionDto(
        String id,
        String userId,
        String sessionId,
        String deviceInfo,
        String deviceId,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        @JsonIgnore
        Set<Permissions> permissions) {
}
