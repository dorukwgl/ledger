package com.doruk.infrastructure.persistence.catalog.tierfeature;

import com.doruk.application.app.catalog.tierfeature.dto.FeatureResponse;
import com.doruk.application.app.catalog.tierfeature.repository.TierFeatureServiceRepo;
import com.doruk.domain.catalog.TierFeature.entity.TierFeature;
import com.doruk.domain.catalog.TierFeature.repository.TierFeatureRepository;
import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.FeatureValue;
import com.doruk.infrastructure.persistence.entity.FeaturesTable;
import com.doruk.infrastructure.persistence.entity.TierFeaturesDraft;
import com.doruk.infrastructure.persistence.entity.TierFeaturesTable;
import com.doruk.infrastructure.persistence.entity.TiersTable;
import com.doruk.jooq.tables.Features;
import com.doruk.jooq.tables.TierFeatures;
import com.doruk.jooq.tables.Tiers;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.jooq.DSLContext;
import org.jooq.Record11;
import org.jooq.SelectOnConditionStep;

import java.util.List;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor
public class TierFeatureRepositoryImpl implements TierFeatureRepository, TierFeatureServiceRepo {
    private final JSqlClient client;
    private final DSLContext dsl;

    private SelectOnConditionStep<Record11<Long, Long, Long, String, String, String, String, String, String, String, CatalogStatus>> generateFeatureResponseSelect(TierFeatures tf) {
        var f = Features.FEATURES;
        var t = Tiers.TIERS;

        return dsl.select(
                        tf.TID,
                        tf.TIER_ID,
                        tf.FEATURE_ID,
                        tf.VALUE,

                        f.CODE.as("featureCode"),
                        f.NAME.as("featureName"),
                        f.DESCRIPTION.as("featureDescription"),
                        f.UNIT,

                        t.CODE.as("tierCode"),
                        t.NAME.as("tierName"),
                        t.STATUS.as("tierStatus")
                )
                .from(tf)
                .innerJoin(t).on(tf.TIER_ID.eq(t.ID))
                .innerJoin(f).on(tf.FEATURE_ID.eq(f.ID));
    }

    private FeatureResponse mapToFeature(Record11<Long, Long, Long, String, String, String, String, String, String, String, com.doruk.domain.catalog.types.CatalogStatus> rs) {
        return FeatureResponse.builder()
                .tid(rs.value1())
                .tierId(rs.value2())
                .featureId(rs.value3())
                .value(rs.value4())
                .featureCode(rs.value5())
                .featureName(rs.value6())
                .featureDescription(rs.value7())
                .unit(rs.value8())
                .tierCode(rs.value9())
                .tierName(rs.value10())
                .tierStatus(rs.value11())
                .build();
    }

    @Override
    public void delete(long tid) {
        client.deleteById(TierFeaturesTable.class, tid);
    }

    @Override
    public boolean tierExists(long id) {
        var t = TiersTable.$;
        return client.createQuery(t)
                .where(t.id().eq(id))
                .exists();
    }

    @Override
    public boolean featureExists(long id) {
        var t = FeaturesTable.$;
        return client.createQuery(t)
                .where(t.id().eq(id))
                .exists();
    }

    @Override
    public boolean tierFeatureExists(long tierId, long featureId) {
        var t = TierFeatures.TIER_FEATURES;
        return dsl.fetchExists(dsl.selectFrom(t)
                .where(t.TIER_ID.eq(tierId).and(t.FEATURE_ID.eq(featureId)))
        );
    }

    @Override
    public FeatureValue getFeatureValue(long featureId) {
        var t = FeaturesTable.$;
        return client.createQuery(t)
                .where(t.id().eq(featureId))
                .select(t.valueType())
                .execute()
                .getFirst();
    }

    @Override
    public FeatureResponse getFeature(long tid) {
        var tf = TierFeatures.TIER_FEATURES;
        return this.generateFeatureResponseSelect(tf)
                .where(tf.TID.eq(tid))
                .fetchOne(this::mapToFeature);
    }

    @Override
    public List<FeatureResponse> getFeatures(long tierId) {
        var tf = TierFeatures.TIER_FEATURES;
        return this.generateFeatureResponseSelect(tf)
                .where(tf.TIER_ID.eq(tierId))
                .fetch(this::mapToFeature);
    }

    @Override
    public TierFeature save(TierFeature tierFeature) {
        var draft = TierFeaturesDraft.$.produce(d -> d
                .setFeaturesId(tierFeature.getFeatureId())
                .setTiersId(tierFeature.getTierId())
                .setValue(tierFeature.getValue())
        );
        var saved = client.saveCommand(draft).execute().getModifiedEntity();
        return TierFeature.rehydrate(
                saved.tid(),
                saved.tiers().id(),
                saved.features().id(),
                saved.value()
        );
    }

    @Override
    public Optional<TierFeature> update(TierFeature tierFeature) {
        var t = TierFeatures.TIER_FEATURES;
        return dsl.update(t)
                .set(t.VALUE, tierFeature.getValue())
                .where(t.TIER_ID.eq(tierFeature.getTierId()).and(t.FEATURE_ID.eq(tierFeature.getFeatureId())))
                .returning(t.TID)
                .fetchOptional()
                .map(u -> TierFeature.rehydrate(
                        u.getTid(),
                        0,
                        0,
                        null
                ));
    }
}
