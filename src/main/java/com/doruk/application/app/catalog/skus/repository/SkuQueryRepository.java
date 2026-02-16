package com.doruk.application.app.catalog.skus.repository;

import com.doruk.application.app.catalog.skus.dto.SkuResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;

import java.util.Optional;
import java.util.UUID;

public interface SkuQueryRepository {
    Optional<SkuResponse> getSku(UUID id);

    PageResponse<SkuResponse> getSkus(long offeringId, PageQuery pageRequest);
}
