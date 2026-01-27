package com.doruk.infrastructure.exceptionhandlers;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Produces  // Ensures it handles JSON-producing routes
@Replaces(ConstraintExceptionHandler.class)
public class CustomValidationExceptionHandler
        implements ExceptionHandler<ConstraintViolationException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, ConstraintViolationException exception) {
        // Build a map of field → error message (or list if multiple per field)
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field.substring(field.lastIndexOf(".") + 1), message);
        }

        // Custom error payload (adjust to your API style)
        Map<String, Object> body = Map.of(
                "error", "Invalid data received...",
                "details", errors
        );

        return HttpResponse.status(HttpStatus.BAD_REQUEST)  // or UNPROCESSABLE_ENTITY (422)
                .body(body);
    }
}