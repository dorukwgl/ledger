package com.doruk.infrastructure.persistence.catalog.offerings;

import com.doruk.application.app.catalog.offerings.dto.OfferingResponse;
import com.doruk.application.app.catalog.offerings.repository.OfferingQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.enums.SortOrder;
import com.doruk.domain.catalog.offerings.entity.Offering;
import com.doruk.domain.catalog.offerings.repository.OfferingRepository;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import com.doruk.infrastructure.persistence.catalog.offerings.mapper.OfferingMapper;
import com.doruk.infrastructure.persistence.entity.OfferingsDraft;
import com.doruk.infrastructure.persistence.entity.OfferingsTable;
import com.doruk.infrastructure.persistence.entity.ProductsTable;
import com.doruk.infrastructure.persistence.mapper.PageMapper;
import io.micronaut.context.annotation.Bean;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.LikeMode;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;

@Singleton
@Bean(typed = OfferingRepository.class)
@RequiredArgsConstructor
public class OfferingRepositoryImpl implements OfferingRepository, OfferingQueryRepository {
    private final JSqlClient client;

    @Override
    public Offering save(Offering offering) {
        var off = OfferingsDraft.$.produce(o -> o
                .setCode(offering.getCode().name())
                .setName(offering.getName())
                .setStatus(offering.getStatus())
                .setDeliveryModel(offering.getDeliveryModel())
                .setInfraModel(offering.getInfraModel())
                .setOperationalOwner(offering.getOperationalOwner())
                .setProductsId(offering.getProductId())
        );
        var saved = client.saveCommand(off).execute().getModifiedEntity();
        return OfferingMapper.toOffering(saved);
    }

    @Override
    public Optional<OfferingResponse> getOffering(String code) {
        var t = OfferingsTable.$;
        return client.createQuery(t)
                .where(t.code().eq(code))
                .select(t, t.products(JoinType.INNER).id(), t.products().code())
                .execute()
                .stream()
                .map(tp -> OfferingMapper.toResponse(tp.get_1(), tp.get_2(), tp.get_3()))
                .findFirst();
    }

    @Override
    public PageResponse<OfferingResponse> getOfferings(@Nullable Long productId, @Nullable String code, PageQuery pageRequest) {
        var t = OfferingsTable.$;
        var page = client.createQuery(t)
                .where(Predicate.and(
                        t.products().id().eqIf(productId),
                        t.code().ilikeIf(code, LikeMode.ANYWHERE)
                ))
                .orderBy(pageRequest.order() == SortOrder.ASC ? t.createdAt().asc() : t.createdAt().desc())
                .select(t, t.products(JoinType.INNER).id(), t.products().code())
                .fetchPage(pageRequest.page(), pageRequest.size());

        return PageMapper.toResponse(page, tpl ->
                OfferingMapper.toResponse(tpl.get_1(), tpl.get_2(), tpl.get_3()));
    }

    @Override
    public Optional<Offering> updateOffering(long id, Offering offering) {
        var draft = OfferingsDraft.$.produce(o -> o
                .setId(id)
                .setProductsId(offering.getProductId())
                .setCode(offering.getCode().name())
                .setName(offering.getName())
                .setUpdatedAt(OffsetDateTime.now())
                .setStatus(offering.getStatus())
                .setDeliveryModel(offering.getDeliveryModel())
                .setInfraModel(offering.getInfraModel())
                .setOperationalOwner(offering.getOperationalOwner())
        );
        var res = client.saveCommand(draft).setMode(SaveMode.UPDATE_ONLY)
                .execute();

        return res.getTotalAffectedRowCount() < 1 ?
                Optional.empty() :
                Optional.of(res.getModifiedEntity())
                        .map(OfferingMapper::toOffering);
    }

    @Override
    public boolean offeringExists(CatalogCode code) {
        return client.createQuery(OfferingsTable.$)
                .where(OfferingsTable.$.code().eq(code.name()))
                .exists();
    }

    @Override
    public boolean offeringExistsExcept(long id, CatalogCode code) {
        return client.createQuery(OfferingsTable.$)
                .where(Predicate.and(
                        OfferingsTable.$.id().ne(id),
                        OfferingsTable.$.code().eq(code.name())
                ))
                .exists();
    }

    @Override
    public void deleteOffering(long id) {
        client.createDelete(OfferingsTable.$)
                .where(OfferingsTable.$.id().eq(id))
                .execute();
    }

    @Override
    public boolean productExists(long id) {
        return client.createQuery(ProductsTable.$)
                .where(ProductsTable.$.id().eq(id))
                .exists();
    }
}
