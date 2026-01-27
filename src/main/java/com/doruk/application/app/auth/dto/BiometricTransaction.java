package com.doruk.application.app.auth.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record BiometricTransaction(
        String userId,
        byte[] publicKey,
        byte[] challenge,
        String ip // for context aware txn
) {
}
