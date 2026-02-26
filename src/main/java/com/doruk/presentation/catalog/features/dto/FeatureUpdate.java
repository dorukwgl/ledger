package com.doruk.presentation.catalog.features.dto;

import com.doruk.domain.catalog.types.FeatureValue;
import io.micrometer.context.Nullable;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record FeatureUpdate(
        @Nullable
        String code,

        @Nullable
        String name,

        @Nullable
        FeatureValue valueType,

        @Nullable
        String description,

        @Nullable
        String unit
) {
}
