package com.doruk.domain.catalog.offerings.repository;

import com.doruk.application.app.catalog.offerings.dto.OfferingResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.domain.catalog.offerings.entity.Offering;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface OfferingRepository {
    Offering save(Offering offering);

    Optional<OfferingResponse> getOffering(String code);

    PageResponse<OfferingResponse> getOfferings(@Nullable Long productId, @Nullable String code, PageQuery pageRequest);

    Optional<Offering> updateOffering(long id, Offering offering);

    boolean offeringExists(CatalogCode code);

    boolean offeringExistsExcept(long id, CatalogCode code);

    void deleteOffering(long id);

    boolean productExists(long id);
}
