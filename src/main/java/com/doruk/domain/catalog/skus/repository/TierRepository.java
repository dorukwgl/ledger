package com.doruk.domain.catalog.skus.repository;

import java.util.Optional;
import java.util.UUID;

import com.doruk.domain.catalog.skus.entity.Tier;
import com.doruk.domain.catalog.valueobjects.CatalogCode;

public interface TierRepository {
    Tier save(Tier tier);

    Optional<Tier> update(long id, Tier tier);

    void delete(long id);

    boolean existsByCode(CatalogCode code);

    boolean skuExists(UUID skuId);

    boolean existsByCodeExcept(long id, CatalogCode code);

}
