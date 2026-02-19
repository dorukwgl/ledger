package com.doruk.domain.catalog.valueobjects;

import com.doruk.domain.exception.InvalidDataShapeException;

import java.util.Arrays;
import java.util.Optional;

public record CatalogCode(String name) {
    public CatalogCode {
        name = Optional.ofNullable(name)
                .map(String::toUpperCase)
                .map(s -> {
                    if (!s.matches("^[A-Z0-9 ]+$"))
                        throw new InvalidDataShapeException("Invalid code format");
                    return s;
                })
                .map(String::strip)
                .map(s -> s.replaceAll("\\s+", "-"))
                .orElse(null);
    }
}
