package com.doruk.application.app.catalog.features;

import java.util.Optional;
import java.util.Set;

import com.doruk.application.app.catalog.features.dto.FeatureResponse;
import com.doruk.application.app.catalog.features.mapper.FeatureMapper;
import com.doruk.application.app.catalog.features.repository.FeatureQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.exception.ForbiddenException;
import com.doruk.application.exception.NotFoundException;
import com.doruk.application.policies.FeaturePolicy;
import com.doruk.domain.catalog.features.entity.Feature;
import com.doruk.domain.catalog.features.repository.FeatureRepository;
import com.doruk.domain.shared.enums.Permissions;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class FeatureService {
    private final FeatureRepository repo;
    private final FeatureQueryRepository repoQuery;

    
    public FeatureResponse getFeature(long id) {
        return repoQuery.getFeature(id)
        .orElseThrow(() -> new NotFoundException("Feature not found"));
    }

    public PageResponse<FeatureResponse> getFeatures(Optional<String> code, PageQuery pageQuery) {
        return repoQuery.getFeatures(code, pageQuery);
    }

    public FeatureResponse createFeature(Feature feature) {
        if (repo.featureExists(feature.getCode()))
            throw new ConflictingArgumentException("Feature code already exists");
        
        return FeatureMapper.toFeatureResponse(repo.save(feature));
    }

    public FeatureResponse updateFeature(long id, Feature feature) {
        if (repo.featureExistsExcept(id, feature.getCode()))
            throw new ConflictingArgumentException("Feature code already exists");
        
        return repo.update(id, feature)
        .map(FeatureMapper::toFeatureResponse)
        .orElseThrow(() -> new NotFoundException("Feature not found"));
    }

    public void deleteFeature(Set<Permissions> permissions, long id) {
        if (!FeaturePolicy.canDeleteFeature(permissions))
            throw new ForbiddenException();
        
        repo.delete(id);
    }
}
