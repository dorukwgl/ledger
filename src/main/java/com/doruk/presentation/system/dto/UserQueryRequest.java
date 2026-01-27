package com.doruk.presentation.system.dto;

import io.micronaut.http.annotation.QueryValue;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;

@Serdeable
public record UserQueryRequest(
        @Nullable
        @QueryValue
        String email
) {
}
