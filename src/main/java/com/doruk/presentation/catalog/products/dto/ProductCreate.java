package com.doruk.presentation.catalog.products.dto;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.Ownership;
import com.doruk.domain.catalog.types.ProductKind;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

@Serdeable
public record ProductCreate(
        @NotBlank
        String code,

        @NotBlank
        String name,

        @Nullable
        String description,

        @NotBlank
        ProductKind productKind,

        @NotBlank
        Ownership ownership,

        @NotBlank
        CatalogStatus status
) {
}
