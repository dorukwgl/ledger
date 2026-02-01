package com.doruk.presentation.catalog.products.dto;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.Ownership;
import com.doruk.domain.catalog.types.ProductKind;
import org.jspecify.annotations.Nullable;

public record ProductUpdate(
        @Nullable
        String code,

        @Nullable
        String name,

        @Nullable
        String description,

        @Nullable
        ProductKind productKind,

        @Nullable
        Ownership ownership,

        @Nullable
        CatalogStatus status
) {
}
