package com.doruk.presentation.catalog.skus.dto;

import java.time.OffsetDateTime;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.SkuCategory;
import com.doruk.domain.catalog.types.SkuScope;

import io.micrometer.context.Nullable;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SkuUpdate(
    @Nullable
    Long offeringId,

    @Nullable
    String code,

    @Nullable
    String name,

    @Nullable
    SkuCategory category,
    
    @Nullable
    SkuScope scope,

    @Nullable
    Boolean isFreemium,
    
    @Nullable
    Integer termHintMonths,
    
    @Nullable
    CatalogStatus status,
    
    @Nullable
    OffsetDateTime publishedAt   
) {
    
}
