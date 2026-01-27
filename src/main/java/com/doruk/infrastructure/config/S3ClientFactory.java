package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Factory
public class S3ClientFactory {
    @Bean
    @Requires(env = "object-storage-s3")
    @Singleton
    S3Client s3Client(S3Config config) {
        return S3Client.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        config.accessKey(),
                                        config.secretKey()
                                )
                        )
                )
                .endpointOverride(URI.create(config.endpoint()))
                .region(Region.of(config.region()))
                .build();
    }

    @Bean
    @Singleton
    S3Presigner s3Presigner(S3Config config) {
        return S3Presigner.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        config.accessKey(),
                                        config.secretKey()
                                )
                        )
                )
                .endpointOverride(URI.create(config.endpoint()))
                .region(Region.of(config.region()))
                .build();
    }
}
