package com.doruk.presentation.catalog.products;

import com.doruk.domain.catalog.products.entity.Product;
import com.doruk.presentation.catalog.products.dto.ProductCreate;
import com.doruk.presentation.catalog.products.dto.ProductUpdate;

public class ProductMapper {
    public static Product toProduct(ProductCreate dto) {
        return Product.create(
                dto.name(),
                dto.code(),
                dto.description(),
                dto.productKind(),
                dto.ownership(),
                dto.status()
        );
    }

    public static Product toProduct(ProductUpdate dto) {
        return Product.create(
                dto.name(),
                dto.code(),
                dto.description(),
                dto.productKind(),
                dto.ownership(),
                dto.status()
        );
    }
}
