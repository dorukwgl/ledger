package com.doruk.infrastructure.apiclient;

import com.doruk.infrastructure.apiclient.dto.JwksRequest;
import com.doruk.infrastructure.apiclient.dto.JwksResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;


@Client("jwks")
public interface JwksClient {
    @Post("/auth/token")
    JwksResponse issueToken(@Body JwksRequest request);
}
