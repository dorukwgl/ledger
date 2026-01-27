package com.doruk.infrastructure.apiclient;

import com.doruk.infrastructure.apiclient.dto.SociairBalanceResponse;
import com.doruk.infrastructure.apiclient.dto.SociairSendSmsResponse;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("sociair")
@Header(name = "accept", value = "application/json")
@Header(name = "content-type", value = "application/json")
@Header(name = HttpHeaders.AUTHORIZATION, value = "Bearer ${micronaut.application.sociairApiKey}")
public interface SociairClient {
    @Post("/api/sms")
    SociairSendSmsResponse sendSms(
            String mobile,
            String message);

    @Get("/api/balance")
    SociairBalanceResponse getBalance();
}
