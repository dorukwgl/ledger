package com.doruk.presentation.catalog.skus.dto;

import java.util.UUID;

import com.doruk.domain.catalog.types.DependencyType;
import com.doruk.domain.catalog.types.EnforcedAt;

import io.micrometer.context.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Parameter;

@Serdeable
public record SkuDependencyUpdate(
    @Parameter(description = "Target or id of sku on which self depends on")
    @Nullable
    UUID depsId,

    @Nullable
    DependencyType dependencyType,

    @Nullable
    EnforcedAt enforcedAt
) {}
