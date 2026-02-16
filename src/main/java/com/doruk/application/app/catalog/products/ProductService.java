package com.doruk.application.app.catalog.products;

import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.application.app.catalog.products.mapper.ProductMapper;
import com.doruk.application.app.catalog.products.repository.ProductQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.exception.ForbiddenException;
import com.doruk.application.exception.NotFoundException;
import com.doruk.application.policies.ProductPolicy;
import com.doruk.domain.catalog.products.entity.Product;
import com.doruk.domain.catalog.products.repository.ProductRepository;
import com.doruk.domain.shared.enums.Permissions;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Singleton
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repo;
    private final ProductQueryRepository repoQuery;

    public ProductResponse getProduct(String code) {
        return repoQuery.getProduct(code)
                .orElseThrow(() -> new NotFoundException("No such product available"));
    }

    public PageResponse<ProductResponse> getAllProducts(String seed, PageQuery pageQuery) {
        return repoQuery.getProducts(seed, pageQuery);
    }

    public ProductResponse createProduct(Product product) {
        if (repo.productExists(product.getCode()))
            throw new ConflictingArgumentException("Product code already exists");

        return ProductMapper.toProductResponse(
                repo.save(product)
        );
    }

    public ProductResponse updateProduct(long id, Product product) {
        if (repo.productExistsExcept(id, product.getCode()))
            throw new ConflictingArgumentException("Product code already exists");

        return repo.updateProduct(id, product)
                .map(ProductMapper::toProductResponse)
                .orElseThrow(() -> new NotFoundException("No such product available"));
    }

    public void deleteProduct(Set<Permissions> permissions, long id) {
        if (!ProductPolicy.canDeleteProduct(permissions))
            throw new ForbiddenException("Insufficient permissions");
        repo.deleteProduct(id);
    }
}
