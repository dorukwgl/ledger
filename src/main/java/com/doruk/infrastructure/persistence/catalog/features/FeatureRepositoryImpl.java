package com.doruk.infrastructure.persistence.catalog.features;

import com.doruk.application.app.catalog.features.dto.FeatureResponse;
import com.doruk.application.app.catalog.features.repository.FeatureQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.enums.SortOrder;
import com.doruk.domain.catalog.features.entity.Feature;
import com.doruk.domain.catalog.features.repository.FeatureRepository;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import com.doruk.infrastructure.persistence.catalog.features.mapper.FeatureMapper;
import com.doruk.infrastructure.persistence.entity.Features;
import com.doruk.infrastructure.persistence.entity.FeaturesDraft;
import com.doruk.infrastructure.persistence.entity.FeaturesTable;
import com.doruk.infrastructure.persistence.mapper.PageMapper;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.LikeMode;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;

import java.util.Optional;

@Singleton
@RequiredArgsConstructor
public class FeatureRepositoryImpl implements FeatureRepository, FeatureQueryRepository {
    private final JSqlClient client;

    @Override
    public Optional<FeatureResponse> getFeature(long id) {
        var t = FeaturesTable.$;
        return client.createQuery(t)
                .where(t.id().eq(id))
                .select(t)
                .execute()
                .stream()
                .map(FeatureMapper::toResponse)
                .findFirst();
    }

    @Override
    public PageResponse<FeatureResponse> getFeatures(Optional<String> code, PageQuery pageQuery) {
        var t = FeaturesTable.$;
        var page = client.createQuery(t)
                .where(t.code().ilikeIf(code.orElse(null), LikeMode.ANYWHERE))
                .orderBy(pageQuery.order() == SortOrder.ASC ? t.createdAt().asc() : t.createdAt().desc())
                .select(t)
                .fetchPage(
                        pageQuery.page(),
                        pageQuery.size()
                );

        return PageMapper.toResponse(page, FeatureMapper::toResponse);
    }

    @Override
    public Feature save(Feature feature) {
        var draft = FeaturesDraft.$.produce(f -> f
                .setName(feature.getName())
                .setDescription(feature.getDescription())
                .setCode(feature.getCode().name())
                .setUnit(feature.getUnit())
                .setValueType(feature.getValueType())
        );

        var saved = client.saveCommand(draft).execute().getModifiedEntity();
        return FeatureMapper.toFeature(saved);
    }

    @Override
    public Optional<Feature> update(long id, Feature feature) {
        var draft = FeaturesDraft.$.produce(f -> f
                .setId(id)
                .setName(feature.getName())
                .setDescription(feature.getDescription())
                .setCode(feature.getCode().name())
                .setUnit(feature.getUnit())
                .setValueType(feature.getValueType())
        );

        var rs = client.saveCommand(draft).setMode(SaveMode.UPDATE_ONLY).execute();
        return rs.getTotalAffectedRowCount() < 1 ?
                Optional.empty() :
                Optional.of(rs.getModifiedEntity())
                        .map(FeatureMapper::toFeature);
    }

    @Override
    public void delete(long id) {
        client.deleteById(Features.class, id);
    }

    @Override
    public boolean featureExists(CatalogCode code) {
        var t = FeaturesTable.$;
        return client.createQuery(t)
                .where(t.code().eq(code.name()))
                .exists();
    }

    @Override
    public boolean featureExistsExcept(long id, CatalogCode code) {
        var t = FeaturesTable.$;
        return client.createQuery(t)
                .where(Predicate.and(
                        t.id().ne(id),
                        t.code().eq(code.name())
                ))
                .exists();
    }
}
