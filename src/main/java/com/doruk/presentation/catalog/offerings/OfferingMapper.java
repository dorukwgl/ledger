package com.doruk.presentation.catalog.offerings;

import com.doruk.domain.catalog.offerings.entity.Offering;
import com.doruk.presentation.catalog.offerings.dto.OfferingCreate;
import com.doruk.presentation.catalog.offerings.dto.OfferingUpdate;

public class OfferingMapper {
    public static Offering toOffering(OfferingCreate dto) {
        return Offering.create(
                dto.productId(),
                dto.code(),
                dto.name(),
                dto.deliveryModel(),
                dto.infraModel(),
                dto.operationalOwner(),
                dto.status()
        );
    }

    public static Offering toOffering(OfferingUpdate dto) {
        return Offering.create(
                dto.productId(),
                dto.code(),
                dto.name(),
                dto.deliveryModel(),
                dto.infraModel(),
                dto.operationalOwner(),
                dto.status()
        );
    }
}
