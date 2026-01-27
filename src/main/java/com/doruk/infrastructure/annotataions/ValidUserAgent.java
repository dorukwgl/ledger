package com.doruk.infrastructure.annotataions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserAgent {
    String message() default "Invalid or missing User-Agent: must identify a known browser, engine, and platform";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
