package com.doruk.presentation.users.dto;

import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfileUpdateRequest(
        @Nullable
        @NotBlank
        @Pattern(
                regexp = "^\\S+(\\s+\\S+)+$",
                message = "Full name must contain at least two words"
        )
        String fullName,

        @Nullable
        @NotBlank
        String address,

        @Nullable
        @NotBlank
        String city,

        @Nullable
        @NotBlank
        String state,

        @Nullable
        @NotBlank
        String country,

        @Nullable
        @NotBlank
        String postal_code
) {
}
