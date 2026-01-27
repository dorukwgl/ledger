package com.doruk.infrastructure.apiclient.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record JwksRequest(String sub, String aud, List<Integer> scp) {
}
