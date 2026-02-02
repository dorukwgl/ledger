package com.doruk.application.app.catalog.products.dto;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.Ownership;
import com.doruk.domain.catalog.types.ProductKind;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.time.OffsetDateTime;

@Serdeable
@Builder
public record ProductResponse(
        String id,
        String name,
        CatalogCode code,
        String description,
        ProductKind productKind,
        Ownership ownership,
        CatalogStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
