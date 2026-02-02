package com.doruk.application.app.auth.dto;

import io.micronaut.core.annotation.Introspected;

import java.time.OffsetDateTime;

@Introspected
public record BiometricDto(
        String userId,
        byte[] publicKey,
        String deviceId,
        OffsetDateTime lastUsedAt
) {
}
