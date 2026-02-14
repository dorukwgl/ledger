package com.doruk.infrastructure.persistence.catalog.skus.mapper;

import com.doruk.application.app.catalog.skus.dto.SkuResponse;
import com.doruk.infrastructure.persistence.entity.Skus;

public class SkuMapper {
    public static SkuResponse toSkuResponse(Skus sku) {
        return SkuResponse.builder()
            .id(sku.id())
            .offeringId(sku.offerings().id())
            .code(sku.code())
            .name(sku.name())
            .scope(sku.scope())
            .isFreemium(sku.isFreemium())
            .category(sku.category())
            .termHintMonths(sku.termHintMonths())
            .trialDays(sku.trialDays())
            .status(sku.status())
            .publishedAt(sku.publishedAt())
            .createdAt(sku.createdAt())
            .updatedAt(sku.updatedAt())
            .build();
    }
}
