package com.doruk.presentation.catalog.tierfeature.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record TierFeatureCreate(
        @NotNull
        long featureId,

        @NotBlank
        String value
) {
}
