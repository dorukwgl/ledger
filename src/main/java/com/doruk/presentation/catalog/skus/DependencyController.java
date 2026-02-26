package com.doruk.presentation.catalog.skus;

import com.doruk.application.app.catalog.skus.SkuDependencyService;
import com.doruk.application.app.catalog.skus.dto.Dependency;
import com.doruk.application.app.catalog.skus.dto.SkuDependency;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.catalog.skus.dto.SkuDependencyCreate;
import com.doruk.presentation.catalog.skus.dto.SkuDependencyUpdate;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Controller("catalog/skus")
@RequiredArgsConstructor
public class DependencyController {
    private final SkuDependencyService service;

    @Operation(description = "List all the dependencies of given sku")
    @Get("/{skuId}/dependencies")
    public List<Dependency> getDependencies(@NotNull UUID skuId) {
        return service.getDependencies(skuId);
    }

    @Operation(description = "Add a new dependency")
    @ApiResponse(responseCode = "404", description = "When one of the sku id is not found")
    @Status(HttpStatus.CREATED)
    @Post("/dependencies")
    public Dependency createDependency(@Valid @Body SkuDependencyCreate dto) {
        return service.createDependency(
                SkuDependency.builder()
                        .skuId(dto.skuId())
                        .depsId(dto.depsId())
                        .dependencyType(dto.dependencyType())
                        .enforcedAt(dto.enforcedAt())
                        .build()
        );
    }

    @Operation(description = "Update the given dependency.")
    @ApiResponse(responseCode = "404", description = "When sku dependency doesn't exists, or target dependency id is not found")
    @Put("/dependencies/{id}")
    public Dependency update(@Valid @Body SkuDependencyUpdate dto,
                             @Parameter(description = "ID of the dependency table")
                             @PathVariable long id) {
        return service.updateDependency(id, SkuDependency.builder()
                .depsId(dto.depsId())
                .dependencyType(dto.dependencyType())
                .enforcedAt(dto.enforcedAt())
                .build()
        );
    }

    @Operation(description = "Delete the given dependency")
    @Delete("dependencies/{id}")
    public InfoResponse delete(@PathVariable long id) {
        service.deleteDependency(id);
        return new InfoResponse("Dependency deleted successfully");
    }
}
