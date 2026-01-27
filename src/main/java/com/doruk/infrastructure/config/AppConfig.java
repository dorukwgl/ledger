package com.doruk.infrastructure.config;


import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("micronaut.application")
public record AppConfig(String name,
                        String publicPathPrefix,
                        String privatePathPrefix,
                        String localStorageDir,
                        String appId,
                        int sessionExpiration,
                        String appUrl,
                        boolean cookieSecure,
                        long imageMaxSize,
                        String resourceApiPath) {
}
