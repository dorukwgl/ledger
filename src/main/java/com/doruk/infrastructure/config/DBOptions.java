package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("r2dbc.datasources.default.options")
public record DBOptions(String schema) {
}
