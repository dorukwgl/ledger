package com.doruk.presentation.catalog.offerings.dto;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.DeliveryModel;
import com.doruk.domain.catalog.types.InfraModelType;
import com.doruk.domain.catalog.types.OperationalOwner;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record OfferingUpdate(
        @Nullable
        Long productId,

        @Nullable
        @NotBlank
        String code,

        @Nullable
        @NotBlank
        String name,

        @Nullable
        DeliveryModel deliveryModel,

        @Nullable
        InfraModelType infraModel,

        @Nullable
        OperationalOwner operationalOwner,

        @Nullable
        CatalogStatus status
) {
}
