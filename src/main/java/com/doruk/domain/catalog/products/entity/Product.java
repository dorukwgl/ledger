package com.doruk.domain.catalog.products.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.Ownership;
import com.doruk.domain.catalog.types.ProductKind;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {
    private Long id;
    private String name;
    private CatalogCode code;
    private String description;
    private ProductKind productKind;
    private Ownership ownership;
    private CatalogStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // for intent/business creation
    public static Product create(String name, String code, String description,
                                 ProductKind productKind, Ownership ownership, CatalogStatus status) {
        return new Product(
                null,
                name,
                new CatalogCode(code),
                description,
                productKind,
                ownership,
                status,
                null,
                null
        );
    }

    // for rehydration factory ( infra/persistence only )
    public static Product rehydrate(
            Long id,
            String name,
            String code,
            String description,
            ProductKind productKind,
            Ownership ownership,
            CatalogStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Product(id, name, new CatalogCode(code), description, productKind,
                ownership, status, createdAt, updatedAt);
    }
}
