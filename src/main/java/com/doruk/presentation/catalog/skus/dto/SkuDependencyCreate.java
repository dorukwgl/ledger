package com.doruk.presentation.catalog.skus.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.UUID;

import com.doruk.domain.catalog.types.DependencyType;
import com.doruk.domain.catalog.types.EnforcedAt;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record SkuDependencyCreate(
    @Parameter(description = "Self sku id, e.g. sku of plugin, extensions etc")
    @NotBlank
    UUID skuId,

    @Parameter(description = "ID of the sku on which the self depends on ( target sku )")
    @NotBlank
    UUID depsId,

    @NotBlank
    DependencyType dependencyType,

    @NotBlank
    EnforcedAt enforcedAt
) {}
