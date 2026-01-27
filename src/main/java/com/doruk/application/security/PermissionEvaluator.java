package com.doruk.application.security;

import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.security.annotation.RequiresPermission;

public interface PermissionEvaluator {
    boolean evaluate(
            UserScope scope,
            Permissions[] required,
            RequiresPermission.Logical logical
    );
}
