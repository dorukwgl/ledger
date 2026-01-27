package com.doruk.application.app.auth.dto;

import com.doruk.application.enums.OtpChannel;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Serdeable
@Builder
public record OtpTransaction(
        String userId,
        int otp,
        OtpChannel channel,
        String target, // address to send otp: phone no or email
        String payload // any additional data, like hold temp email for updating new email
) {
}
