package com.doruk.application.app.users.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.time.OffsetDateTime;

@Serdeable
@Builder
public record ProfileDto(
        String userId,
        String fullName,
        String address,
        String city,
        String state,
        String country,
        String postalCode,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
