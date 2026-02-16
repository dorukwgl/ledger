package com.doruk.domain.catalog.skus.repository;

import com.doruk.domain.catalog.skus.entity.Sku;
import com.doruk.domain.catalog.valueobjects.CatalogCode;

import java.util.Optional;
import java.util.UUID;

public interface SkuRepository {
    Sku save(Sku sku);

    Optional<Sku> update(UUID id, Sku sku);

    void delete(UUID id);

    boolean existsByCode(CatalogCode code);

    boolean offeringExists(long offeringId);

    boolean existsByCodeExcept(UUID id, CatalogCode code);

    boolean hasConflictingDefaultTier(UUID skuId, long candidateTierId);

    void updateDefaultTier(UUID skuId, long tierId, boolean isDefault);
}