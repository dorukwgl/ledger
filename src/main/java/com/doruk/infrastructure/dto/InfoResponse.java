package com.doruk.infrastructure.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record InfoResponse(String message) {
}
