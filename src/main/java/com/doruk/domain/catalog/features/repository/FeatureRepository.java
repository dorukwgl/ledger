package com.doruk.domain.catalog.features.repository;

import java.util.Optional;

import com.doruk.domain.catalog.features.entity.Feature;
import com.doruk.domain.catalog.valueobjects.CatalogCode;

public interface FeatureRepository {
    Feature save(Feature feature);
    Optional<Feature> update(long id, Feature feature);
    void delete(long id);

    boolean featureExists(CatalogCode code);

    boolean featureExistsExcept(long id, CatalogCode code);
}
