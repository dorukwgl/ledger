package com.doruk.presentation.catalog.features;

import java.util.Optional;

import com.doruk.application.app.catalog.features.FeatureService;
import com.doruk.application.app.catalog.features.dto.FeatureResponse;
import com.doruk.application.dto.PageResponse;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.catalog.features.dto.FeatureCreate;
import com.doruk.presentation.catalog.features.dto.FeatureUpdate;
import com.doruk.presentation.catalog.features.mapper.FeatureMapper;
import com.doruk.presentation.dto.PageQueryMapper;
import com.doruk.presentation.dto.PageQueryRequest;
import com.doruk.presentation.utils.AuthUtils;

import io.micrometer.context.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller("/catalog/features")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
public class FeatureController {
    private final FeatureService service;

    @Operation(description = "Get a feature by id")
    @Get("/{id}")
    public FeatureResponse getFeature(long id) {
        return service.getFeature(id);
    }

    @Operation(description = "Get features. If code is provided, returns features matching the given code")
    @Get("/{?}")
    public PageResponse<FeatureResponse> getFeatures(@QueryValue @Nullable String code, @Valid @QueryValue PageQueryRequest pageQuery) {
        return service.getFeatures(Optional.ofNullable(code), PageQueryMapper.toQuery(pageQuery));
    }

    @Operation(description = "Create a new feature")
    @ApiResponse(responseCode = "409", description = "Feature already exists")
    @Status(HttpStatus.CREATED)
    @Post
    public FeatureResponse createFeature(@Valid @Body FeatureCreate dto) {
        return service.createFeature(FeatureMapper.toFeature(dto));
    }

    @Operation(description = "Update the given feature")
    @ApiResponse(responseCode = "404", description = "Feature not found")
    @ApiResponse(responseCode = "409", description = "Feature already exists")
    @Put
    public FeatureResponse updateFeature(long id, @Valid @Body FeatureUpdate dto) {
        return service.updateFeature(id, FeatureMapper.toFeature(dto));
    }

    @Operation(description = "Delete given feature")
    @Delete("/{id}")
    public InfoResponse deleteFeature(Authentication auth, long id) {
        service.deleteFeature(AuthUtils.extractPermissions(auth), id);
        return new InfoResponse("Feature deleted successfully");
    }
}
