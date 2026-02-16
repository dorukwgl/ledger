package com.doruk.domain.catalog.skus.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tier {
    private Long id;
    private UUID skuId;
    private String name;
    private CatalogCode code;
    private boolean isDefault;
    private CatalogStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


    public static Tier create(UUID skuId, String name, String code, CatalogStatus status) {
        return new Tier(null, skuId, name, new CatalogCode(code), false, status, null, null);
    }

    public static Tier rehydrate(Long id, UUID skuId, String name, String code, boolean isDefault, CatalogStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Tier(id, skuId, name, new CatalogCode(code), isDefault, status, createdAt, updatedAt);
    }
}
