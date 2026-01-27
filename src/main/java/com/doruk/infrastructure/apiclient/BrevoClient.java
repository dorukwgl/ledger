package com.doruk.infrastructure.apiclient;

import com.doruk.infrastructure.apiclient.dto.BrevoMailRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;

@Client("brevo")
@Header(name = "accept", value = "application/json")
@Header(name = "content-type", value = "application/json")
public interface BrevoClient {
    @Serdeable
    record BrevoResponse(String messageId, String message, String code){};

    @Post("/v3/smtp/email")
    BrevoResponse sendEmail(@Header("api-key") String apiKey, @Body BrevoMailRequest body);
}
