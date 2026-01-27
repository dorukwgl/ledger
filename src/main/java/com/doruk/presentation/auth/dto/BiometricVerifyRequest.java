package com.doruk.presentation.auth.dto;

import com.doruk.infrastructure.annotataions.ValidUserAgent;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Header;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BiometricVerifyRequest(
        @Parameter(description = "Alias for deviceId ( from login request ). Refer to login request.")
        @Parameter(description = "Not to be confused with biometric's deviceId")
        @Nullable
        @Header("X-Device-Id")
        @Size(max = 200, min = 32)
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        String notifId,

        @Parameter(description = "Biometric device identifier, the one sent during biometric enrollment")
        @NotBlank
        String deviceId,

        @Parameter(required = false)
        @NotBlank
        @Header("User-Agent")
        @ValidUserAgent
        String userAgent,

        @Parameter(description = "The challenge sent during biometric initiation.")
        @NotBlank
        String challenge,

        @Parameter(description = "Base64 URL encoded (without padding) string value of the SHA256withECDSA signature bytes")
        @NotBlank
        String signature
) {
}
