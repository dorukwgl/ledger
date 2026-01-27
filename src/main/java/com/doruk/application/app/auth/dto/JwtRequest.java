package com.doruk.application.app.auth.dto;

import com.doruk.domain.shared.enums.Permissions;
import io.micronaut.core.annotation.Introspected;

import java.util.Set;

@Introspected
public record JwtRequest(String id, String audience, Set<Permissions> permissions) {
}
