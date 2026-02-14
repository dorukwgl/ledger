package com.doruk.presentation.catalog.offerings;

import com.doruk.application.app.catalog.offerings.OfferingService;
import com.doruk.application.app.catalog.offerings.dto.OfferingResponse;
import com.doruk.application.dto.PageResponse;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.catalog.offerings.dto.OfferingCreate;
import com.doruk.presentation.catalog.offerings.dto.OfferingUpdate;
import com.doruk.presentation.dto.PageQueryMapper;
import com.doruk.presentation.dto.PageQueryRequest;
import com.doruk.presentation.utils.AuthUtils;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Controller("/catalog/offerings")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService service;

    @Operation(description = "Fetch the offering using its code")
    @Get("/{code}")
    public OfferingResponse getOffering(String code) {
        return service.getOffering(code);
    }

    @Operation(description = "List all offerings, offerings of specific product " +
            "or search using offering code")
    @Get("{/productId}{?}")
    public PageResponse<OfferingResponse> getOffering(
            @Nullable
            @NotBlank
            Long productId,
            @Nullable
            @NotBlank
            @QueryValue
            @Parameter(description = "Offering Code to search for")
            String code,
            @Valid
            @RequestBean
            PageQueryRequest pageQueryRequest
    ) {
        return service.getAllOfferings(productId, code,
                PageQueryMapper.toQuery(pageQueryRequest));
    }

    @Operation(description = "Create a new offering")
    @ApiResponse(responseCode = "409", description = "Offering code already exists")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @Status(HttpStatus.CREATED)
    @Post
    public OfferingResponse createOffering(@Valid @Body OfferingCreate dto) {
        return service.createOffering(OfferingMapper.toOffering(dto));
    }

    @Operation(description = "Update existing offering")
    @ApiResponse(responseCode = "409", description = "Offering code already exists")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "404", description = "Offering not found")
    @Put("/{id}")
    public OfferingResponse updateOffering(long id, @Valid @Body OfferingUpdate dto) {
        return service.updateOffering(id, OfferingMapper.toOffering(dto));
    }

    @Operation(description = "Delete given offering")
    @Delete("/{id}")
    public InfoResponse deleteOffering(Authentication auth, long id) {
        service.deleteOffering(AuthUtils.extractPermissions(auth), id);
        return new InfoResponse("Offering deleted successfully");
    }
}
