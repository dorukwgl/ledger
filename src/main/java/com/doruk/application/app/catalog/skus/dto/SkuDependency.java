package com.doruk.application.app.catalog.skus.dto;


import java.util.UUID;

import com.doruk.domain.catalog.types.DependencyType;
import com.doruk.domain.catalog.types.EnforcedAt;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Serdeable
@Builder
public record SkuDependency(
    UUID skuId,
    UUID depsId,
    DependencyType dependencyType,
    EnforcedAt enforcedAt
) {}
