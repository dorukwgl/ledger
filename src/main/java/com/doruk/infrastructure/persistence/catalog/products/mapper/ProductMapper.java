package com.doruk.infrastructure.persistence.catalog.products.mapper;

import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.domain.catalog.products.entity.Product;
import com.doruk.infrastructure.persistence.entity.Products;
import io.micronaut.context.annotation.Mapper;

public interface ProductMapper {
    @Mapper
    ProductResponse toResponse(Products product);

    default Product toProduct(Products p) {
        return Product.rehydrate(
                p.id(),
                p.name(),
                p.code(),
                p.description(),
                p.productKind(),
                p.ownership(),
                p.status(),
                p.createdAt(),
                p.updatedAt()
        );
    }
}
