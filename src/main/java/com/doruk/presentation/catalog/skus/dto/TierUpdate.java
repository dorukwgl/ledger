package com.doruk.presentation.catalog.skus.dto;

import java.util.UUID;

import com.doruk.domain.catalog.types.CatalogStatus;

import io.micrometer.context.Nullable;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record TierUpdate(
    @Nullable
    UUID skuId,

    @Nullable
    String name,

    @Nullable
    String code,
   
    @Nullable
    CatalogStatus status
) {
}
