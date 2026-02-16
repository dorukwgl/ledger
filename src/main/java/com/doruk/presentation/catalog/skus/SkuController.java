package com.doruk.presentation.catalog.skus;

import java.util.UUID;

import com.doruk.application.app.catalog.skus.SkuService;
import com.doruk.application.app.catalog.skus.dto.SkuResponse;
import com.doruk.application.dto.PageResponse;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.catalog.skus.dto.SkuCreate;
import com.doruk.presentation.catalog.skus.dto.SkuUpdate;
import com.doruk.presentation.catalog.skus.mappers.SkuMapper;
import com.doruk.presentation.dto.PageQueryMapper;
import com.doruk.presentation.dto.PageQueryRequest;
import com.doruk.presentation.utils.AuthUtils;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Controller("/catalog/skus")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
public class SkuController {
    private final SkuService service;
    
    
    @Operation(description = "Fetch the sku using its id")
    @Get("/{id}")
    public SkuResponse getSku(UUID id) {
        return service.getSku(id);
    }

    @Operation(description = "List all skus of a given offering")
    @Get("/{offeringId}{?}")
    public PageResponse<SkuResponse> getSkus(@NotNull @PathVariable long offeringId, 
        @Valid @RequestBean PageQueryRequest pageQueryRequest) {
        return service.getSkus(offeringId, PageQueryMapper.toQuery(pageQueryRequest));
    }

    @Operation(description = "Create a new SKU")
    @ApiResponse(responseCode = "409", description = "SKU already exists")
    @ApiResponse(responseCode = "404", description = "Offering not found")
    @Status(HttpStatus.CREATED)
    @Post
    public SkuResponse createSku(@Valid @Body SkuCreate dto) {
        return service.createSku(SkuMapper.toSku(dto));
    }

    @Operation(description = "Update existing SKU")
    @ApiResponse(responseCode = "409", description = "SKU already exists")
    @ApiResponse(responseCode = "404", description = "Offering not found")
    @ApiResponse(responseCode = "404", description = "SKU not found")
    @Put("/{id}")
    public SkuResponse updateSku(UUID id, @Valid @Body SkuUpdate dto) {
        return service.updateSku(id, SkuMapper.toSku(dto));
    }

    @Operation(description = "Delete given SKU")
    @Delete("/{id}")
    public InfoResponse deleteSku(Authentication auth, @PathVariable UUID id) {
        service.deleteSku(AuthUtils.extractPermissions(auth), id);
        return new InfoResponse("SKU deleted successfully");
    }

    @Operation(description = "Update/Change the default tier of given SKU." +
            "Note: Only one default tier can exist per SKU, so make sure to update previous default to false if any")
    @ApiResponse(responseCode = "409", description = "Another default tier already exists.")
    @Put("/{skuId}/update-default/{tierId}/{defaultState}")
    public InfoResponse updateDefaultSkuTier(@PathVariable UUID skuId, @PathVariable long tierId, @PathVariable boolean defaultState) {
        service.updateDefaultSkuTier(skuId,  tierId, defaultState);
        return new InfoResponse("Tier updated successfully");
    }
}
