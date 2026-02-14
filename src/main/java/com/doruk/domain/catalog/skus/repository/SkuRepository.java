package com.doruk.domain.catalog.skus.repository;

import java.util.Optional;
import java.util.UUID;

import com.doruk.application.app.catalog.skus.dto.SkuResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.domain.catalog.skus.entity.Sku;
import com.doruk.domain.catalog.valueobjects.CatalogCode;

public interface SkuRepository {
    Sku save(Sku sku);

    Optional<SkuResponse> getSku(UUID id);

    PageResponse<SkuResponse> getSkus(long offeringId, PageQuery pageRequest);

    Optional<Sku> update(UUID id, Sku sku);
    
    void delete(UUID id);

    boolean existsByCode(CatalogCode code);

    boolean offeringExists(long offeringId);
    
    boolean existsByCodeExcept(UUID id, CatalogCode code);
}

