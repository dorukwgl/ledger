package com.doruk.application.app.catalog.tierfeature.dto;

import com.doruk.domain.catalog.types.CatalogStatus;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Builder
@Serdeable
public record FeatureResponse(
        long tid,
        long tierId,
        long featureId,

        String value,

        // feature
        String featureCode,
        String featureName,
        String featureDescription,
        String unit,

        // tier
        String tierCode,
        String tierName,
        CatalogStatus tierStatus
) {
}
