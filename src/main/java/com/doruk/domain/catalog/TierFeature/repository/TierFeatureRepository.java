package com.doruk.domain.catalog.TierFeature.repository;

import com.doruk.domain.catalog.TierFeature.entity.TierFeature;

import java.util.Optional;

public interface TierFeatureRepository {
    TierFeature save(TierFeature tierFeature);
    Optional<TierFeature> update(TierFeature tierFeature);
}
