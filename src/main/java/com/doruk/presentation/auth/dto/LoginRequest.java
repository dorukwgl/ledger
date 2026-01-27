package com.doruk.presentation.auth.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record LoginRequest(
        @NotBlank
        @Size(max = 150, min = 1)
        String identifier,

        @NotBlank
        @Size(max = 150, min = 1)
        String password
) {
}
