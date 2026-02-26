package com.doruk.presentation.catalog.features.dto;

import com.doruk.domain.catalog.types.FeatureValue;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record FeatureCreate(
        @NotBlank
        String code,

        @NotBlank
        String name,

        @NotNull
        FeatureValue valueType,

        @NotBlank
        String description,

        @NotBlank
        String unit
) {
}
