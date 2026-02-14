package com.doruk.application.app.catalog.skus.mapper;

import com.doruk.application.app.catalog.skus.dto.SkuResponse;
import com.doruk.domain.catalog.skus.entity.Sku;

public class SkuMapper {
    public static SkuResponse toSkuResponse(Sku sku) {
        return SkuResponse.builder()
                .id(sku.getId())
                .offeringId(sku.getOfferingId())
                .code(sku.getCode().name())
                .name(sku.getName())
                .status(sku.getStatus())
                .category(sku.getCategory())
                .scope(sku.getScope())
                .isFreemium(sku.isFreemium())
                .termHintMonths(sku.getTermHintMonths())
                .publishedAt(sku.getPublishedAt())
                .createdAt(sku.getCreatedAt())
                .updatedAt(sku.getUpdatedAt())
                .build();
    }
}
