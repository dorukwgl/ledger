package com.doruk.infrastructure.security.mappers;

import com.doruk.application.app.auth.dto.JwtResponse;
import com.doruk.infrastructure.apiclient.dto.JwksResponse;
import io.micronaut.context.annotation.Mapper;

public interface JwksTokenMapper {
    @Mapper
    JwtResponse toTokenResponse(JwksResponse jwksResponse);
}
