package com.doruk.presentation.catalog.skus.mappers;

import com.doruk.domain.catalog.skus.entity.Tier;
import com.doruk.presentation.catalog.skus.dto.TierCreate;
import com.doruk.presentation.catalog.skus.dto.TierUpdate;

public class TierMapper {
    
    public static Tier toTier(TierCreate dto) {
        return Tier.create(dto.skuId(), dto.name(), dto.code(), dto.status());
    }

    public static Tier toTier(TierUpdate dto) {
        return Tier.create(dto.skuId(), dto.name(), dto.code(), dto.status());
    }
}
