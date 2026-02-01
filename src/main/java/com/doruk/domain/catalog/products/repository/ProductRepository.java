package com.doruk.domain.catalog.products.repository;

import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.domain.catalog.products.entity.Product;
import com.doruk.domain.catalog.valueobjects.CatalogCode;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<ProductResponse> getProduct(String code);

    PageResponse<ProductResponse> getProducts(@Nullable String code, PageQuery pageRequest);

    Optional<Product> updateProduct(long id, Product product);

    boolean productExists(CatalogCode code);

    boolean productExistsExcept(long id, CatalogCode code);

    void deleteProduct(long id);
}
