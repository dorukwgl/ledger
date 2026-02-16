package com.doruk.presentation.catalog.skus.mappers;

import com.doruk.domain.catalog.skus.entity.Sku;
import com.doruk.presentation.catalog.skus.dto.SkuCreate;
import com.doruk.presentation.catalog.skus.dto.SkuUpdate;

public class SkuMapper {
    public static Sku toSku(SkuCreate dto) {
        return Sku.create(
                dto.offeringId(),
                dto.code(),
                dto.name(),
                dto.category(),
                dto.scope(),
                dto.isFreemium(),
                dto.termHintMonths(),
                dto.trialDays(),
                dto.status(),
                dto.publishedAt()
        );
    }

    public static Sku toSku(SkuUpdate dto) {
        return Sku.create(
                dto.offeringId(),
                dto.code(),
                dto.name(),
                dto.category(),
                dto.scope(),
                dto.isFreemium(),
                dto.termHintMonths(),
                dto.trialDays(),
                dto.status(),
                dto.publishedAt()
        );
    }
}
