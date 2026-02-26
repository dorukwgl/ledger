package com.doruk.application.app.catalog.skus.dto;

import java.time.OffsetDateTime;

import com.doruk.domain.catalog.types.DependencyType;
import com.doruk.domain.catalog.types.EnforcedAt;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Serdeable
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Dependency(
    long id,
    String depsId,
    String depsCode,
    DependencyType dependencyType,
    EnforcedAt enforcedAt,
    OffsetDateTime createdAt
) {}
