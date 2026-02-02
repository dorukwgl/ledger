package com.doruk.application.app.auth.dto;

import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.domain.shared.enums.Permissions;
import io.micronaut.core.annotation.Introspected;
import lombok.Builder;

import java.util.Set;

@Introspected
@Builder
public record AuthDto(
        String id,
        String username,
        String password,
        String email,
        String phone,
        boolean emailVerified,
        boolean phoneVerified,
        MultiAuthType multiFactorAuth,
        Set<Permissions> permissions) {
}
