package com.doruk.application.app.users.dto;

import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.domain.shared.enums.UserAccountStatus;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Serdeable
@Builder
public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String phone,
        UserAccountStatus status,
        boolean emailVerified,
        boolean phoneVerified,
        MultiAuthType multiFactorAuth,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

}
