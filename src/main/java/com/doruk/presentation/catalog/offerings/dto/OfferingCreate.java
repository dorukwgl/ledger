package com.doruk.presentation.catalog.offerings.dto;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.DeliveryModel;
import com.doruk.domain.catalog.types.InfraModelType;
import com.doruk.domain.catalog.types.OperationalOwner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OfferingCreate(
        @NotNull
        Long productId,

        @NotBlank
        String code,

        @NotBlank
        String name,

        @NotNull
        DeliveryModel deliveryModel,

        @NotNull
        InfraModelType infraModel,

        @NotNull
        OperationalOwner operationalOwner,

        @NotNull
        CatalogStatus status
) {
}
