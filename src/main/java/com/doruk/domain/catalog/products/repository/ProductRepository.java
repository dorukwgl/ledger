package com.doruk.domain.catalog.products.repository;

import com.doruk.domain.catalog.products.entity.Product;
import com.doruk.domain.catalog.valueobjects.CatalogCode;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> updateProduct(long id, Product product);

    boolean productExists(CatalogCode code);

    boolean productExistsExcept(long id, CatalogCode code);

    void deleteProduct(long id);
}
