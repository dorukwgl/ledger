package com.doruk.domain.catalog.offerings.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.DeliveryModel;
import com.doruk.domain.catalog.types.InfraModelType;
import com.doruk.domain.catalog.types.OperationalOwner;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Offering {
    private Long id;
    private Long productId;
    private CatalogCode code;
    private String name;
    private DeliveryModel deliveryModel;
    private InfraModelType infraModel;
    private OperationalOwner operationalOwner;
    private CatalogStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // public intent
    public static Offering create(
            Long productId,
            String code,
            String name,
            DeliveryModel deliveryModel,
            InfraModelType infraModel,
            OperationalOwner operationalOwner,
            CatalogStatus status
    ) {
        return new Offering(
                null,
                productId,
                new CatalogCode(code),
                name,
                deliveryModel,
                infraModel,
                operationalOwner,
                status,
                null,
                null
        );
    }

    // rehydrate, factory // persistence only
    public static Offering rehydrate(
            Long id,
            Long productId,
            String code,
            String name,
            DeliveryModel deliveryModel,
            InfraModelType infraModel,
            OperationalOwner operationalOwner,
            CatalogStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Offering(
          id,
          productId,
          new CatalogCode(code),
          name,
          deliveryModel,
          infraModel,
          operationalOwner,
          status,
          createdAt,
          updatedAt
        );
    }
}
