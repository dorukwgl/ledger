package com.doruk.domain.catalog.offerings.repository;

import com.doruk.domain.catalog.offerings.entity.Offering;
import com.doruk.domain.catalog.valueobjects.CatalogCode;

import java.util.Optional;

public interface OfferingRepository {
    Offering save(Offering offering);

    Optional<Offering> updateOffering(long id, Offering offering);

    boolean offeringExists(CatalogCode code);

    boolean offeringExistsExcept(long id, CatalogCode code);

    void deleteOffering(long id);

    boolean productExists(long id);
}
