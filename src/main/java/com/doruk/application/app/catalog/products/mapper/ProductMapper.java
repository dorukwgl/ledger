package com.doruk.application.app.catalog.products.mapper;

import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.domain.catalog.products.entity.Product;

public class ProductMapper {
    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .code(product.getCode().name())
                .description(product.getDescription())
                .productKind(product.getProductKind())
                .ownership(product.getOwnership())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
