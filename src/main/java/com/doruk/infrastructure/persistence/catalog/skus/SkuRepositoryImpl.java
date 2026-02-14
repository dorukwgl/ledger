package com.doruk.infrastructure.persistence.catalog.skus;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import com.doruk.application.app.catalog.skus.dto.SkuResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.enums.SortOrder;
import com.doruk.domain.catalog.skus.entity.Sku;
import com.doruk.domain.catalog.skus.repository.SkuRepository;
import com.doruk.domain.catalog.valueobjects.CatalogCode;

import com.doruk.infrastructure.persistence.catalog.skus.mapper.SkuMapper;
import com.doruk.infrastructure.persistence.entity.OfferingsTable;
import com.doruk.infrastructure.persistence.entity.SkusDraft;
import com.doruk.infrastructure.persistence.entity.SkusTable;
import com.doruk.infrastructure.persistence.mapper.PageMapper;
import com.doruk.jooq.tables.Skus;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jooq.DSLContext;

@Singleton
@RequiredArgsConstructor
public class SkuRepositoryImpl implements SkuRepository {
    private final DSLContext dsl;
    private final JSqlClient client;

    @Override
    public Sku save(Sku sku) {
        var s = Skus.SKUS;
        return dsl.insertInto(s)
                .set(dsl.newRecord(s)
                                .setOfferingId(sku.getOfferingId())
                                .setName(sku.getName())
                                .setCode(sku.getCode().name())
                                .setCategory(sku.getCategory())
                                .setIsFreemium(sku.isFreemium())
                                .setScope(sku.getScope())
                                .setTrialDays(sku.getTrialDays())
                                .setTermHintMonths(sku.getTermHintMonths())
                                .setStatus(sku.getStatus())
                                .setPublishedAt(sku.getPublishedAt())
                )
                .returning(s)
                .fetchOne(rs ->
                        Sku.rehydrate(
                                rs.getId(),
                                rs.getOfferingId(),
                                rs.getCode(),
                                rs.getName(),
                                rs.getCategory(),
                                rs.getScope(),
                                rs.getIsFreemium(),
                                rs.getTermHintMonths(),
                                rs.getTrialDays(),
                                rs.getStatus(),
                                rs.getPublishedAt(),
                                rs.getCreatedAt(),
                                rs.getUpdatedAt()
                        ));
    }

    @Override
    public Optional<SkuResponse> getSku(UUID id) {
        var t = SkusTable.$;
        return client.createQuery(t)
                .where(t.id().eq(id))
                .select(t)
                .execute()
                .stream()
                .map(SkuMapper::toSkuResponse)
                .findFirst();
    }

    @Override
    public PageResponse<SkuResponse> getSkus(long offeringId, PageQuery pageRequest) {
        var t = SkusTable.$;
        var page = client.createQuery(t)
                .where(t.offeringsId().eq(offeringId))
                .orderBy(pageRequest.order() == SortOrder.ASC ?
                        t.createdAt().asc() :
                        t.createdAt().desc())
                .select(t)
                .fetchPage(
                        pageRequest.page(),
                        pageRequest.size()
                );
        return PageMapper.toResponse(page, SkuMapper::toSkuResponse);
    }

    @Override
    public Optional<Sku> update(UUID id, Sku sku) {
        var draft = SkusDraft.$.produce(s -> s
                .setName(sku.getName())
                .setCode(sku.getCode().name())
                .setCategory(sku.getCategory())
                .setScope(sku.getScope())
                .setTrialDays(sku.getTrialDays())
                .setTermHintMonths(sku.getTermHintMonths())
                .setFreemium(sku.isFreemium())
                .setOfferingsId(sku.getOfferingId())
                .setStatus(sku.getStatus())
                .setPublishedAt(sku.getPublishedAt())
                .setUpdatedAt(OffsetDateTime.now())
        );

        var rs = client.saveCommand(draft).setMode(SaveMode.UPDATE_ONLY).execute();

        return rs.getTotalAffectedRowCount() < 1 ?
                Optional.empty() :
                Optional.of(rs.getModifiedEntity())
                        .map(s -> Sku.rehydrate(
                                s.id(),
                                s.offerings().id(),
                                s.code(),
                                s.name(),
                                s.category(),
                                s.scope(),
                                s.isFreemium(),
                                s.termHintMonths(),
                                s.trialDays(),
                                s.status(),
                                s.publishedAt(),
                                s.createdAt(),
                                s.updatedAt()
                        ));
    }

    @Override
    public void delete(UUID id) {
        var t = SkusTable.$;
        client.createDelete(t)
                .where(t.id().eq(id))
                .execute();
    }

    @Override
    public boolean existsByCode(CatalogCode code) {
        var t = SkusTable.$;
        return client.createQuery(t)
                .where(t.code().eq(code.name()))
                .exists();
    }

    @Override
    public boolean offeringExists(long offeringId) {
        var t = OfferingsTable.$;
        return client.createQuery(t)
                .where(t.id().eq(offeringId))
                .exists();
    }

    @Override
    public boolean existsByCodeExcept(UUID id, CatalogCode code) {
        var t = SkusTable.$;
        return client.createQuery(t)
                .where(Predicate.and(
                        t.id().ne(id),
                        t.code().eq(code.name())
                ))
                .exists();
    }
    
}
