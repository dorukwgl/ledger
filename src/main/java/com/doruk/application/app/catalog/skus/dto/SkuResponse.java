package com.doruk.application.app.catalog.skus.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.SkuCategory;
import com.doruk.domain.catalog.types.SkuScope;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Builder
@Serdeable
public record SkuResponse(
    UUID id,
    long offeringId,
    String code,
    String name,
    SkuCategory category,
    SkuScope scope,
    boolean isFreemium,
    Integer termHintMonths,
    Integer trialDays,
    CatalogStatus status,
    OffsetDateTime publishedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    
}
