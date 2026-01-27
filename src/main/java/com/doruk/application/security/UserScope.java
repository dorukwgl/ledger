package com.doruk.application.security;

import com.doruk.domain.shared.enums.Permissions;

import java.util.Set;

public record UserScope(String userId, Set<Permissions> permissions) {
    public static final String KEY = "userScope";
}
