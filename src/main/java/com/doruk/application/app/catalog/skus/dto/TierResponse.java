package com.doruk.application.app.catalog.skus.dto;

import java.time.OffsetDateTime;

import com.doruk.domain.catalog.types.CatalogStatus;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Builder
@Serdeable
public record TierResponse(
    Long id,
    String skuId,
    String name,
    String code,
    boolean isDefault,
    CatalogStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
