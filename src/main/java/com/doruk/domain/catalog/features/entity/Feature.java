package com.doruk.domain.catalog.features.entity;

import com.doruk.domain.catalog.types.FeatureValue;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Feature {
    private Long id;
    private CatalogCode code;
    private String name;
    private FeatureValue valueType;
    private String description;
    private String unit;
    private OffsetDateTime createdAt;

    public static Feature create(String code, String name, FeatureValue valueType, String description, String unit) {
        return new Feature(null, new CatalogCode(code), name, valueType, description, unit, null);
    }

    public static Feature rehydrate(long id, String code, String name, FeatureValue valueType, String description, String unit, OffsetDateTime createdAt) {
        return new Feature(id, new CatalogCode(code), name, valueType, description, unit, createdAt);
    }
}
