package com.doruk.application.app.catalog.offerings.mapper;

import com.doruk.application.app.catalog.offerings.dto.OfferingResponse;
import com.doruk.domain.catalog.offerings.entity.Offering;

public class OfferingMapper {
    public static OfferingResponse toOfferingResponse(Offering offering) {
        return OfferingResponse.builder()
                .id(offering.getId())
                .name(offering.getName())
                .productId(offering.getProductId())
                .code(offering.getCode().name())
                .status(offering.getStatus())
                .infraModel(offering.getInfraModel())
                .operationalOwner(offering.getOperationalOwner())
                .updatedAt(offering.getUpdatedAt())
                .createdAt(offering.getCreatedAt())
                .deliveryModel(offering.getDeliveryModel())
                .build();
    }
}
