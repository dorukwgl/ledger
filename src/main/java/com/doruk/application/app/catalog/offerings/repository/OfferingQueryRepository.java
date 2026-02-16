package com.doruk.application.app.catalog.offerings.repository;

import com.doruk.application.app.catalog.offerings.dto.OfferingResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface OfferingQueryRepository {
    Optional<OfferingResponse> getOffering(String code);

    PageResponse<OfferingResponse> getOfferings(@Nullable Long productId, @Nullable String code, PageQuery pageRequest);
}
