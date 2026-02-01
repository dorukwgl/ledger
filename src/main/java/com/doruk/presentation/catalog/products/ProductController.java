package com.doruk.presentation.catalog.products;

import com.doruk.application.app.catalog.products.ProductService;
import com.doruk.application.app.catalog.products.dto.ProductResponse;
import com.doruk.application.dto.PageResponse;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.catalog.products.dto.ProductCreate;
import com.doruk.presentation.catalog.products.dto.ProductUpdate;
import com.doruk.presentation.dto.PageQueryMapper;
import com.doruk.presentation.dto.PageQueryRequest;
import com.doruk.presentation.utils.AuthUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@Controller("/catalog/products")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @Get
    public PageResponse<ProductResponse> getProducts(@QueryValue @Nullable String seed, @Valid PageQueryRequest pageRequest) {
        return service.getAllProducts(seed, PageQueryMapper.toQuery(pageRequest));
    }

    @Get("/{code}")
    public ProductResponse getProduct(@PathVariable String code) {
        return service.getProduct(code);
    }

    @Status(HttpStatus.CREATED)
    @Operation(description = "Create a new product")
    @ApiResponse(responseCode = "409", description = "Product code already exists")
    @Post
    public ProductResponse createProduct(@Body @Valid ProductCreate dto) {
        return service.createProduct(ProductMapper.toProduct(dto));
    }

    @Operation(description = "Update existing product")
    @ApiResponse(responseCode = "409", description = "Product code already exists")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @Put("/{id}")
    public ProductResponse updateProduct(@PathVariable long id, @Body @Valid ProductUpdate dto) {
        return service.updateProduct(id, ProductMapper.toProduct(dto));
    }

    @Delete("/{id}")
    public InfoResponse deleteProduct(Authentication auth, @PathVariable long id) {
        service.deleteProduct(AuthUtils.extractPermissions(auth), id);
        return new InfoResponse("Product deleted successfully");
    }
}
