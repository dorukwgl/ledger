package com.doruk.application.app.catalog.features.mapper;

import com.doruk.application.app.catalog.features.dto.FeatureResponse;
import com.doruk.domain.catalog.features.entity.Feature;

public class FeatureMapper {
    public static FeatureResponse toFeatureResponse(Feature dto) {
        return FeatureResponse.builder()
            .id(dto.getId())
            .code(dto.getCode().name())
            .name(dto.getName())
            .valueType(dto.getValueType())
            .description(dto.getDescription())
            .unit(dto.getUnit())
            .createdAt(dto.getCreatedAt())
            .build();
    }
}
