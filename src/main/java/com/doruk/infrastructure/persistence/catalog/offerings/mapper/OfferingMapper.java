package com.doruk.infrastructure.persistence.catalog.offerings.mapper;

import com.doruk.application.app.catalog.offerings.dto.OfferingResponse;
import com.doruk.domain.catalog.offerings.entity.Offering;
import com.doruk.infrastructure.persistence.entity.Offerings;

public class OfferingMapper {
    public static OfferingResponse toResponse(Offerings offering, Long productId, String productCode) {
        return OfferingResponse.builder()
                .id(offering.id())
                .name(offering.name())
                .code(offering.code())
                .productId(productId)
                .productCode(productCode)
                .deliveryModel(offering.deliveryModel())
                .infraModel(offering.infraModel())
                .operationalOwner(offering.operationalOwner())
                .status(offering.status())
                .createdAt(offering.createdAt())
                .updatedAt(offering.updatedAt())
                .build();
    }

    public static Offering toOffering(Offerings o) {
        return Offering.rehydrate(
                o.id(),
                o.products().id(),
                o.code(),
                o.name(),
                o.deliveryModel(),
                o.infraModel(),
                o.operationalOwner(),
                o.status(),
                o.createdAt(),
                o.updatedAt()
        );
    }
}
