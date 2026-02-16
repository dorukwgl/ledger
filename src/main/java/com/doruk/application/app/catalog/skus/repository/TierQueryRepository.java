package com.doruk.application.app.catalog.skus.repository;

import java.util.UUID;

import com.doruk.application.app.catalog.skus.dto.TierResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;

public interface TierQueryRepository {
    PageResponse<TierResponse> getTiers(UUID skuId, PageQuery pageRequest);
}
