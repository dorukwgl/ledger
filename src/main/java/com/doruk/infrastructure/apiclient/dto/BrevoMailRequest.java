package com.doruk.infrastructure.apiclient.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;

@Serdeable
public record BrevoMailRequest(
        MailUser sender,
        List<MailUser> to,
        String subject,
        String htmlContent,
        Map<String, Object> params
) {
    @Serdeable
    public record MailUser(String name, String email) {
    }
}
