package com.doruk.presentation.catalog.skus.dto;

import java.util.UUID;

import com.doruk.domain.catalog.types.CatalogStatus;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record TierCreate(
    @NotBlank
    UUID skuId,

    @NotBlank
    String name,

    @NotBlank
    String code,

    @NotNull
    CatalogStatus status
) {
}
