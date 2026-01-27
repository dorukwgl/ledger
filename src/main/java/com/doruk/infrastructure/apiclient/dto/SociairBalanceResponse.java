package com.doruk.infrastructure.apiclient.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SociairBalanceResponse(
        String balance,
        String credit,
        String limit,
        String errors
) {
}
