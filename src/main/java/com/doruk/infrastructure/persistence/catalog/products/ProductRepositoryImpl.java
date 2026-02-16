package com.doruk.infrastructure.persistence.catalog.products;

import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.application.app.catalog.products.repository.ProductQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.enums.SortOrder;
import com.doruk.domain.catalog.products.entity.Product;
import com.doruk.domain.catalog.products.repository.ProductRepository;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import com.doruk.infrastructure.persistence.catalog.products.mapper.ProductMapper;
import com.doruk.infrastructure.persistence.entity.ProductsDraft;
import com.doruk.infrastructure.persistence.entity.ProductsTable;
import com.doruk.infrastructure.persistence.mapper.PageMapper;
import io.micronaut.context.annotation.Bean;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.LikeMode;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;

@Singleton
@Bean(typed = ProductRepository.class)
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository, ProductQueryRepository {
    private final JSqlClient client;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        var pr = ProductsDraft.$.produce(p -> p
                .setCode(product.getCode().name())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setOwnership(product.getOwnership())
                .setProductKind(product.getProductKind())
                .setStatus(product.getStatus())
        );

        var generated = client.saveCommand(pr).execute().getModifiedEntity();
        return productMapper.toProduct(generated);
    }

    @Override
    public Optional<ProductResponse> getProduct(String code) {
        var t = ProductsTable.$;
        return client.createQuery(t)
                .where(t.code().eq(code))
                .select(t)
                .execute()
                .stream()
                .map(productMapper::toResponse)
                .findFirst();
    }

    @Override
    public PageResponse<ProductResponse> getProducts(@Nullable String seed, PageQuery pageRequest) {
        var t = ProductsTable.$;
        var page = client.createQuery(t)
                .where(
                        Predicate.or(
                                t.code().ilikeIf(seed, LikeMode.ANYWHERE),
                                t.name().ilikeIf(seed, LikeMode.ANYWHERE)
                        )
                )
                .orderBy(pageRequest.order() == SortOrder.ASC ?
                        t.createdAt().asc() :
                        t.createdAt().desc())
                .select(t)
                .fetchPage(
                        pageRequest.page(),
                        pageRequest.size()
                );
        return PageMapper.toResponse(page, productMapper::toResponse);
    }

    @Override
    public Optional<Product> updateProduct(long id, Product product) {
        var draft = ProductsDraft.$.produce(p -> p
                .setId(id)
                .setCode(product.getCode().name())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setOwnership(product.getOwnership())
                .setProductKind(product.getProductKind())
                .setStatus(product.getStatus())
                .setUpdatedAt(OffsetDateTime.now())
        );
        var res = client.saveCommand(draft).setMode(SaveMode.UPDATE_ONLY)
                .execute();

        return res.getTotalAffectedRowCount() < 1 ?
                Optional.empty() :
                Optional.of(res.getModifiedEntity())
                        .map(productMapper::toProduct);
    }

    @Override
    public boolean productExists(CatalogCode code) {
        var t = ProductsTable.$;
        return client.createQuery(t)
                .where(t.code().eq(code.name()))
                .exists();
    }

    @Override
    public boolean productExistsExcept(long id, CatalogCode code) {
        var t = ProductsTable.$;
        return client.createQuery(t)
                .where(Predicate.and(
                        t.code().eq(code.name()),
                        t.id().ne(id)
                ))
                .exists();
    }

    @Override
    public void deleteProduct(long id) {
        var t = ProductsTable.$;
        client.createDelete(t)
                .where(t.id().eq(id))
                .execute();
    }
}
