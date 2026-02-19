package com.doruk.application.app.catalog.features.dto;

import com.doruk.domain.catalog.types.FeatureValue;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.time.OffsetDateTime;

@Serdeable
@Builder
public record FeatureResponse(
        Long id,
        String code,
        String name,
        FeatureValue valueType,
        String description,
        String unit,
        OffsetDateTime createdAt
) {
}
