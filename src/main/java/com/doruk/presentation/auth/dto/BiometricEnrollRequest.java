package com.doruk.presentation.auth.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record BiometricEnrollRequest(
        @Parameter(description = "Device to uniquely identify the current physical device. It should be sent during the subsequent biometric logins.")
        @Parameter(description = "Not to be confused with deviceId for notifications, its different.")
        @NotBlank
        @Size(min = 64, max = 72)
        String deviceId,

        @Parameter(description = "Public key, with curve p256 and alg: ECDSA.")
        @Parameter(description = "Format: Base64-encoded raw EC public key (X || Y)")
        @NotBlank
        @Size(max = 200)
        String publicKey
) {
}
