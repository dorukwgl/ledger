package com.doruk.application.app.auth.dto;

import com.doruk.domain.shared.enums.MultiAuthType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Serdeable
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String accessToken,
        String accessTokenType,
        int accessTokenExpiresIn,
        String refreshToken,
        boolean isEmailVerified,
        boolean isPhoneVerified,
        boolean mfaRequired,
        MultiAuthType mfaType,
        String mfaToken,
        int mfaExpiresIn
) {
}
