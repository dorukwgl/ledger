package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("jimmer")
public record JimmerConfig(boolean showSql, String dialect) {
}
