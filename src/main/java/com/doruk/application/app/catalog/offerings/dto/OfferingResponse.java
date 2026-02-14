package com.doruk.application.app.catalog.offerings.dto;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.DeliveryModel;
import com.doruk.domain.catalog.types.InfraModelType;
import com.doruk.domain.catalog.types.OperationalOwner;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.time.OffsetDateTime;

@Serdeable
@Builder
public record OfferingResponse(
        Long id,
        Long productId,
        String code,
        String name,
        String productCode,
        DeliveryModel deliveryModel,
        InfraModelType infraModel,
        OperationalOwner operationalOwner,
        CatalogStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
