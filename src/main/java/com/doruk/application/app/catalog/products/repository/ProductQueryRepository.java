package com.doruk.application.app.catalog.products.repository;

import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface ProductQueryRepository {
    Optional<ProductResponse> getProduct(String code);

    PageResponse<ProductResponse> getProducts(@Nullable String code, PageQuery pageRequest);
}
