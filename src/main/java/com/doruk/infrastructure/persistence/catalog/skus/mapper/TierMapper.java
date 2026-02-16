package com.doruk.infrastructure.persistence.catalog.skus.mapper;

import com.doruk.application.app.catalog.skus.dto.TierResponse;
import com.doruk.domain.catalog.skus.entity.Tier;
import com.doruk.infrastructure.persistence.entity.Tiers;

public class TierMapper {
    public static TierResponse toTierResponse(Tiers dto) {
        return TierResponse.builder()
            .id(dto.id())
            .skuId(dto.skus().id().toString())
            .code(dto.code())
            .name(dto.name())
            .isDefault(dto.isDefaults())
            .status(dto.status())
            .createdAt(dto.createdAt())
            .updatedAt(dto.updatedAt())
        .build();
    }

    public static Tier toTier(Tiers dto) {
        return Tier.rehydrate(
                dto.id(),
                dto.skus().id(),
                dto.name(),
                dto.code(),
                dto.isDefaults(),
                dto.status(),
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}
