package com.doruk.application.app.catalog.features.repository;

import java.util.Optional;

import com.doruk.application.app.catalog.features.dto.FeatureResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;

public interface FeatureQueryRepository {
    Optional<FeatureResponse> getFeature(long id);
    PageResponse<FeatureResponse> getFeatures(Optional<String> code, PageQuery pageQuery);
}
