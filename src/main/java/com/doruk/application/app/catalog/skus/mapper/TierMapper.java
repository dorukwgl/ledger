package com.doruk.application.app.catalog.skus.mapper;

import com.doruk.application.app.catalog.skus.dto.TierResponse;
import com.doruk.domain.catalog.skus.entity.Tier;

public class TierMapper {
    public static TierResponse toTierResponse(Tier tier) {
        return TierResponse.builder()
                .id(tier.getId())
                .skuId(tier.getSkuId().toString())
                .code(tier.getCode().name())
                .name(tier.getName())
                .isDefault(tier.isDefault())
                .status(tier.getStatus())
                .createdAt(tier.getCreatedAt())
                .updatedAt(tier.getUpdatedAt())
                .build();
    }
}
