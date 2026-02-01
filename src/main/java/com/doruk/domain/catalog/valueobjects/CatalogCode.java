package com.doruk.domain.catalog.valueobjects;

import java.util.Arrays;
import java.util.Optional;

public record CatalogCode(String name) {
    public CatalogCode {
        name = Optional.ofNullable(name)
                .map(String::toUpperCase)
                .map(String::strip)
                .map(s -> s.replaceAll("\\s+", " "))
                .map(s -> s.split(" "))
                .stream()
                .flatMap(Arrays::stream)
                .reduce((a, b) -> a + "-" + b)
                .orElse(null);
    }
}
