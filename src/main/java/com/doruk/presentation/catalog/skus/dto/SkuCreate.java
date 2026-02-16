package com.doruk.presentation.catalog.skus.dto;

import java.time.OffsetDateTime;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.SkuCategory;
import com.doruk.domain.catalog.types.SkuScope;

import io.micrometer.context.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record SkuCreate(
    @NotNull
    long offeringId,

    @NotBlank
    String code,

    @NotBlank
    String name,

    @NotNull
    SkuCategory category,
    
    @NotNull
    SkuScope scope,

    boolean isFreemium,
    
    @Nullable
    Integer termHintMonths,

    @Nullable
    Integer trialDays,
    
    @NotNull
    CatalogStatus status,
    
    @NotNull
    OffsetDateTime publishedAt
) {
}
