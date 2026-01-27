package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("brevo")
public record BrevoConfig(String url, String apiKey, String senderEmail, String senderName)  {
}
