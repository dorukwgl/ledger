package com.doruk.application.app.catalog.skus;

import com.doruk.application.app.catalog.skus.dto.Dependency;
import com.doruk.application.app.catalog.skus.dto.SkuDependency;
import com.doruk.application.exception.InvalidInputException;
import com.doruk.infrastructure.persistence.catalog.skus.SkuDependencyRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class SkuDependencyService {
    private final SkuDependencyRepository repo;

    public List<Dependency> getDependencies(UUID skuId) {
        return repo.getDependencies(skuId);
    }

    public Dependency createDependency(SkuDependency dto) {
        if (!(repo.skuExists(dto.skuId()) && repo.skuExists(dto.depsId())))
            throw new InvalidInputException("SKU not found");

        if (dto.skuId().equals(dto.depsId()))
            throw new InvalidInputException("Circular dependency found");

        return repo.save(dto);
    }

    public Dependency updateDependency(long id, SkuDependency dto) {
        if (dto.depsId() != null && !repo.skuExists(dto.depsId()))
            throw new InvalidInputException("Dependency SKU not found");

        return repo.update(id, dto)
                .orElseThrow(() -> new InvalidInputException("SKU not found"));
    }

    public void deleteDependency(long id) {
        repo.delete(id);
    }
}
