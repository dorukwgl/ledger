package com.doruk.infrastructure.apiclient.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record JwksResponse(String accessToken, String tokenType, int expiresIn) {
}
