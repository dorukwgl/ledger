package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;

@ConfigurationProperties("storage.s3")
@Requires(env = "object-storage-s3")
public record S3Config(
        String bucket,
        String publicBaseUrl,
        String region,
        String endpoint,
        String accessKey,
        String secretKey
) {
}
