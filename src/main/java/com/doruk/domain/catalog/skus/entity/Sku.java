package com.doruk.domain.catalog.skus.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.SkuCategory;
import com.doruk.domain.catalog.types.SkuScope;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sku {
    private UUID id;
    private long offeringId;
    private CatalogCode code;
    private String name;
    private SkuCategory category;
    private SkuScope scope;
    private boolean isFreemium;
    private Integer termHintMonths; // nullable
    private Integer trialDays;
    private CatalogStatus status;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static Sku create(
        long offeringId,
        String code,
        String name,
        SkuCategory category,
        SkuScope scope,
        boolean isFreemium,
        Integer termHintMonths,
        Integer trialDays,
        CatalogStatus status,
        OffsetDateTime publishedAt
    ) {
        return new Sku(
            UUID.randomUUID(),
            offeringId,
            new CatalogCode(code),
            name,
            category,
            scope,
            isFreemium,
            termHintMonths,
            trialDays,
            status,
            publishedAt,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }

    public static Sku rehydrate(
        UUID id,
        long offeringId,
        String code,
        String name,
        SkuCategory category,
        SkuScope scope,
        boolean isFreemium,
        Integer termHintMonths,
        Integer trialDays,
        CatalogStatus status,
        OffsetDateTime publishedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
    ) {
        return new Sku(
            id,
            offeringId,
            new CatalogCode(code),
            name,
            category,
            scope,
            isFreemium,
            termHintMonths,
            trialDays,
            status,
            publishedAt,
            createdAt,
            updatedAt
        );
    }
}
