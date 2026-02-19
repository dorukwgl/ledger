package com.doruk.presentation.catalog.features.mapper;

import com.doruk.domain.catalog.features.entity.Feature;
import com.doruk.presentation.catalog.features.dto.FeatureCreate;
import com.doruk.presentation.catalog.features.dto.FeatureUpdate;

public class FeatureMapper {
    public static Feature toFeature(FeatureCreate dto) {
        return Feature.create(
            dto.code(), 
            dto.name(), 
            dto.valueType(), 
            dto.description(), 
            dto.unit()
        );
    }

    public static Feature toFeature(FeatureUpdate dto) {
        return Feature.create(
            dto.code(), 
            dto.name(), 
            dto.valueType(), 
            dto.description(), 
            dto.unit()
        );
    }
}
