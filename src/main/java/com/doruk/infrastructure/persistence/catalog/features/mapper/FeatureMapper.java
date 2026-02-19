package com.doruk.infrastructure.persistence.catalog.features.mapper;

import com.doruk.application.app.catalog.features.dto.FeatureResponse;
import com.doruk.domain.catalog.features.entity.Feature;
import com.doruk.infrastructure.persistence.entity.Features;

public class FeatureMapper {
    public static Feature toFeature(Features dto) {
        return Feature.rehydrate(
            dto.id(),
            dto.code(),
            dto.name(),
            dto.valueType(),
            dto.description(),
            dto.unit(),
            dto.createdAt()
        );
    }

    public static FeatureResponse toResponse(Features dto) {
        return FeatureResponse.builder()
            .id(dto.id())
            .code(dto.code())
            .name(dto.name())
            .valueType(dto.valueType())
            .description(dto.description())
            .unit(dto.unit())
            .createdAt(dto.createdAt())
            .build();
    }
}
