package com.doruk.presentation.utils;

import com.doruk.application.security.UserScope;
import com.doruk.domain.shared.enums.Permissions;
import io.micronaut.security.authentication.Authentication;

import java.util.Set;

public class AuthUtils {
    public static Set<Permissions> extractPermissions(Authentication auth) {
        return (Set<Permissions>) auth.getAttributes().get(UserScope.KEY);
    }
}