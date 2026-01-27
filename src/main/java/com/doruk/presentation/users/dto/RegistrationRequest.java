package com.doruk.presentation.users.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.*;

import java.util.Locale;

@Serdeable
public record RegistrationRequest(
        @NotBlank
        @Size(min = 3, max = 30, message = "Username must be between 3 to 30 letters")
        String username,

        @NotBlank
        @Size(min = 8, max = 50, message = "Password must be 8 to 50 letters")
        String password,

        @NotBlank
        @Email(message = "Unidentifiable email format")
        String email,

        @Nullable
        @Pattern(
//                regexp = "^\\+?[0-9]{1,4}[\\s-]?(\\([0-9]+\\)|[0-9])[0-9\\s-]*$",
                regexp = "^\\+?\\d{6,14}$\n",
                message = "Invalid phone number"
        )
        String phone
) {
}
