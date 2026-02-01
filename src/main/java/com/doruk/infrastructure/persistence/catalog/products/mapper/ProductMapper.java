package com.doruk.infrastructure.persistence.catalog.products.mapper;

import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.infrastructure.persistence.entity.Products;
import io.micronaut.context.annotation.Mapper;

public interface ProductMapper {
    @Mapper
    ProductResponse toResponse(Products product);
}
