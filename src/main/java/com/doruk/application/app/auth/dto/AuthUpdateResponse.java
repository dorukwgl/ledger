package com.doruk.application.app.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthUpdateResponse(
    String tid,
    boolean otpRequired,
    String message
) {
}
