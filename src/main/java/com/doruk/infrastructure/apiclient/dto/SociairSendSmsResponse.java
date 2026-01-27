package com.doruk.infrastructure.apiclient.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SociairSendSmsResponse(
        String message
) {
}
