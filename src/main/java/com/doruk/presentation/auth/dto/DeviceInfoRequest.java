package com.doruk.presentation.auth.dto;

import com.doruk.infrastructure.annotataions.ValidUserAgent;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Header;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Serdeable
public record DeviceInfoRequest(
        @Parameter(description = "Firebase notification device id. This is used to send notifications. " +
                "Or random unique device identifier in case of web browser." +
                "Can be different in each sessions.")
        @Parameter(description = "Not to be confused with biometric's deviceId")
        @Nullable
        @Header("X-Device-Id")
        @Size(max = 200, min = 32)
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        String deviceId,

        @Parameter(required = false)
        @NotBlank
        @Header("User-Agent")
        @ValidUserAgent
        String userAgent
) {
}
