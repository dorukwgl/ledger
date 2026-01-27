package com.doruk.infrastructure.util;

import io.micronaut.http.HttpRequest;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class ErrorBuilder {
    public static Map<String, Object> buildErrorBody(HttpRequest<?> request, String message, int status) {
        return Map.of(
                "error", Objects.requireNonNullElse(message, "No error message available..."),
                "status", status,
                "path", request.getPath(),
                "timestamp", Instant.now().toString()
        );
    }
}
