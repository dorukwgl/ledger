package com.doruk.infrastructure.security;

import com.doruk.application.security.PermissionEvaluator;
import com.doruk.application.security.UserScope;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.security.annotation.RequiresPermission;
import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@Singleton
public class RbacPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean evaluate(
            UserScope scope,
            Permissions[] required,
            RequiresPermission.Logical logical
    ) {
        if (required.length == 0) {
            return true;
        }

        if (isDictator(scope))
            return true;

        if (isDictatorRequired(required))
            return false;

        return switch (logical) {
            case AND -> hasAll(scope, required);
            case OR -> hasAny(scope, required);
        };
    }

    private boolean isDictator(UserScope scope) {
        return scope.permissions().contains(Permissions.DICTATOR_PERMISSION);
    }

    private boolean isDictatorRequired(Permissions[] required) {
        return Arrays.stream(required).anyMatch(p -> p == Permissions.DICTATOR_PERMISSION);
    }

    private boolean hasAll(UserScope scope, Permissions[] required) {
        return scope.permissions().containsAll(Arrays.stream(required).toList());
    }

    private boolean hasAny(UserScope scope, Permissions[] required) {
        return Arrays.stream(required)
                .anyMatch(scope.permissions()::contains);
    }
}
