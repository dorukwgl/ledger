package com.doruk.infrastructure.security.annotation;

import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.security.interceptor.RequiresPermissionInterceptor;
import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Around
@Type(RequiresPermissionInterceptor.class)
public @interface RequiresPermission {
    enum Logical {
        AND,
        OR
    }

    Permissions[] value();

    Logical logical() default Logical.AND;
}