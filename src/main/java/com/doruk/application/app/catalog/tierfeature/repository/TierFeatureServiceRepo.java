package com.doruk.application.app.catalog.tierfeature.repository;

import com.doruk.application.app.catalog.tierfeature.dto.FeatureResponse;
import com.doruk.domain.catalog.types.FeatureValue;

import java.util.List;

public interface TierFeatureServiceRepo {
    // id of join table
    void delete(long tid);

    boolean tierExists(long id);

    boolean featureExists(long id);

    boolean tierFeatureExists(long tierId, long featureId);

    FeatureValue getFeatureValue(long featureId);

    // tid is the id of the join table
    FeatureResponse getFeature(long tid);

    List<FeatureResponse> getFeatures(long tierId);
}
