package com.doruk.presentation.catalog.tierfeature;

import com.doruk.application.app.catalog.tierfeature.TierFeatureService;
import com.doruk.application.app.catalog.tierfeature.dto.FeatureResponse;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.catalog.tierfeature.dto.TierFeatureCreate;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller("catalog/tiers")
@RequiredArgsConstructor
public class TierFeatureController {
    private final TierFeatureService service;

    @Operation(description = "Create tier feature")
    @Status(HttpStatus.CREATED)
    @Post("/{tierId}/features")
    public FeatureResponse createTierFeature(@PathVariable long tierId,
                                             @Valid @Body TierFeatureCreate dto) {
        return service.createTierFeature(tierId, dto.featureId(), dto.value());
    }

    @Operation(description = "Get features of given tier")
    @Get("/{tierId}/features")
    public List<FeatureResponse> getTierFeatures(@PathVariable long tierId) {
        return service.getFeatures(tierId);
    }

    @Operation(description = "Update the Tier Feature, only value can be updated")
    @Put("/{tierId}/features/{featureId}")
    public FeatureResponse updateTierFeature(@PathVariable long tierId,
                                             @PathVariable long featureId,
                                             @Body @NotBlank String value) {
        return service.updateTierFeature(tierId, featureId, value);
    }

    @Operation(description = "Delete given tier feature")
    @Delete("/{tid}")
    public InfoResponse deleteTierFeature(@PathVariable long tid) {
        service.deleteTierFeature(tid);
        return new InfoResponse("Successfully deleted tier feature");
    }
}
