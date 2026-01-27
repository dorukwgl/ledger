package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("r2dbc.datasources.default")
public record DatabaseConfig(String url, String username, String password, String dialect, DBOptions options) {
}
