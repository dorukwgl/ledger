package com.doruk.infrastructure.security.interceptor;

import com.doruk.application.exception.ForbiddenException;
import com.doruk.application.security.PermissionEvaluator;
import com.doruk.application.security.UserScope;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.security.annotation.RequiresPermission;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Singleton;

@Singleton
public class RequiresPermissionInterceptor
        implements MethodInterceptor<Object, Object> {

    private final SecurityService securityService;
    private final PermissionEvaluator evaluator;

    public RequiresPermissionInterceptor(
            SecurityService securityService,
            PermissionEvaluator evaluator
    ) {
        this.securityService = securityService;
        this.evaluator = evaluator;
    }

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        var annotation =
                context.findAnnotation(RequiresPermission.class)
                        .orElseThrow(() -> new IllegalArgumentException("Missing @RequiresPermission annotation"));

        Authentication auth = securityService.getAuthentication()
                .orElseThrow(ForbiddenException::new);

        UserScope scope = extractScope(auth);
        boolean allowed = evaluator.evaluate(
                scope,
                annotation.enumValues("value", Permissions.class),
                annotation.enumValue("logical", RequiresPermission.Logical.class).orElseThrow()
        );

        if (!allowed)
            throw new ForbiddenException();

        return context.proceed();
    }

    private UserScope extractScope(Authentication authentication) {
        Object scope = authentication.getAttributes().get(UserScope.KEY);

        if (!(scope instanceof UserScope userScope))
            throw new IllegalStateException("UserScope missing in Authentication");

        return userScope;
    }
}
