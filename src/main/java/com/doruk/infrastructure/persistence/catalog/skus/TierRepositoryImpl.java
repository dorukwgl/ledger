package com.doruk.infrastructure.persistence.catalog.skus;

import com.doruk.application.app.catalog.skus.dto.TierResponse;
import com.doruk.application.app.catalog.skus.repository.TierQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.enums.SortOrder;
import com.doruk.domain.catalog.skus.entity.Tier;
import com.doruk.domain.catalog.skus.repository.TierRepository;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import com.doruk.infrastructure.persistence.catalog.skus.mapper.TierMapper;
import com.doruk.infrastructure.persistence.entity.SkusTable;
import com.doruk.infrastructure.persistence.entity.TiersDraft;
import com.doruk.infrastructure.persistence.entity.TiersTable;
import com.doruk.infrastructure.persistence.mapper.PageMapper;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class TierRepositoryImpl implements TierRepository, TierQueryRepository {
    private final JSqlClient client;

    @Override
    public PageResponse<TierResponse> getTiers(UUID skuId, PageQuery pageRequest) {
        var t = TiersTable.$;
        var page = client.createQuery(t)
                .where(t.skusId().eq(skuId))
                .orderBy(pageRequest.order() == SortOrder.ASC ?
                        t.createdAt().asc() :
                        t.createdAt().desc())
                .select(t)
                .fetchPage(
                        pageRequest.page(),
                        pageRequest.size()
                );
        return PageMapper.toResponse(page, TierMapper::toTierResponse);
    }

    @Override
    public Tier save(Tier tier) {
        var draft = TiersDraft.$.produce(t -> t
                .setSkusId(tier.getSkuId())
                .setName(tier.getName())
                .setCode(tier.getCode().name())
                .setStatus(tier.getStatus())
                .setDefaults(tier.isDefault())
        );
        var saved = client.saveCommand(draft)
                .execute()
                .getModifiedEntity();
        return TierMapper.toTier(saved);
    }

    @Override
    public Optional<Tier> update(long id, Tier tier) {
        var draft = TiersDraft.$.produce(t -> t
                .setId(id)
                .setSkusId(tier.getSkuId())
                .setName(tier.getName())
                .setCode(tier.getCode().name())
                .setStatus(tier.getStatus())
                .setDefaults(tier.isDefault())
                .setUpdatedAt(OffsetDateTime.now())
        );

        var rs = client.saveCommand(draft).setMode(SaveMode.UPDATE_ONLY).execute();

        return rs.getTotalAffectedRowCount() < 1 ?
                Optional.empty() :
                Optional.of(rs.getModifiedEntity())
                        .map(TierMapper::toTier);
    }

    @Override
    public void delete(long id) {
        client.deleteById(TiersTable.class, id);
    }

    @Override
    public boolean existsByCode(CatalogCode code) {
        var t = TiersTable.$;
        return client.createQuery(t)
                .where(t.code().eq(code.name()))
                .exists();
    }

    @Override
    public boolean skuExists(UUID skuId) {
        var t = SkusTable.$;
        return client.createQuery(t)
                .where(t.id().eq(skuId))
                .exists();
    }

    @Override
    public boolean existsByCodeExcept(long id, CatalogCode code) {
        var t = TiersTable.$;
        return client.createQuery(t)
                .where(Predicate.and(
                        t.id().ne(id),
                        t.code().eq(code.name())
                ))
                .exists();
    }
}
