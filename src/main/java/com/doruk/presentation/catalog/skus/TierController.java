package com.doruk.presentation.catalog.skus;

import java.util.UUID;

import com.doruk.application.app.catalog.skus.TierService;
import com.doruk.application.app.catalog.skus.dto.TierResponse;
import com.doruk.application.dto.PageResponse;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.catalog.skus.dto.TierCreate;
import com.doruk.presentation.catalog.skus.dto.TierUpdate;
import com.doruk.presentation.catalog.skus.mappers.TierMapper;
import com.doruk.presentation.dto.PageQueryMapper;
import com.doruk.presentation.dto.PageQueryRequest;
import com.doruk.presentation.utils.AuthUtils;
import com.oracle.svm.core.annotate.Delete;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.RequestBean;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller("catalog/tiers")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
public class TierController {
    private final TierService service;

    @Operation(description = "Get all tiers of a given SKU")
    @Get("/{skuId}{?}")
    public PageResponse<TierResponse> getTiers(UUID skuId, @Valid @RequestBean PageQueryRequest request) {
        return service.getTiers(skuId, PageQueryMapper.toQuery(request));
    }

    @Operation(description = "Create a new tier")
    @ApiResponse(responseCode = "409", description = "Tier already exists")
    @ApiResponse(responseCode = "404", description = "SKU not found")
    @Status(HttpStatus.CREATED)
    @Post
    public TierResponse createTier(@Valid @Body TierCreate dto) {
        return service.createTier(TierMapper.toTier(dto));
    }

    @Operation(description = "Update existing tier")
    @ApiResponse(responseCode = "404", description = "Tier not found")
    @ApiResponse(responseCode = "404", description = "SKU not found")
    @ApiResponse(responseCode = "409", description = "Tier already exists")
    @Put("/{id}")
    public TierResponse updateTier(long id, @Valid @Body TierUpdate dto) {
        return service.updateTier(id, TierMapper.toTier(dto));
    }

    @Operation(description = "Delete given tier")
    @Delete("/{id}")
    public InfoResponse deleteTier(Authentication auth, long id) {
        service.deleteTier(AuthUtils.extractPermissions(auth), id);
        return new InfoResponse("Tier deleted successfully");
    }
}
