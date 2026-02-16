package com.doruk.application.app.catalog.skus;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import com.doruk.application.app.catalog.skus.dto.SkuResponse;
import com.doruk.application.app.catalog.skus.mapper.SkuMapper;
import com.doruk.application.app.catalog.skus.repository.SkuQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.exception.ForbiddenException;
import com.doruk.application.exception.InvalidInputException;
import com.doruk.application.exception.NotFoundException;
import com.doruk.application.policies.SkuPolicy;
import com.doruk.domain.catalog.skus.entity.Sku;
import com.doruk.domain.catalog.skus.repository.SkuRepository;
import com.doruk.domain.shared.enums.Permissions;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class SkuService {
    private final SkuRepository repo;
    private final SkuQueryRepository repoQuery;

    private void canCreateOrUpdateSku(Predicate<Sku> skuExist, Sku sku) {
        var skuExists = skuExist.test(sku);
        
        // null check for updates, where offering id can be null
        var offExists = Optional.of(sku.getOfferingId())
                .map(repo::offeringExists)
                .orElse(true);

        if (!offExists)
            throw new InvalidInputException("Offering not found");
            
        if (skuExists)
            throw new ConflictingArgumentException("Product sku code already exists");
    }

    private void canSetDefaultTier(UUID skuId, long tierId) {
        if (repo.hasConflictingDefaultTier(skuId, tierId))
            throw new ConflictingArgumentException("Default Tier already exists for given SKU");
    }

    public SkuResponse getSku(UUID id) {
        return repoQuery.getSku(id)
                .orElseThrow(NotFoundException::new);
    }

    public PageResponse<SkuResponse> getSkus(long offeringId, PageQuery pageRequest) {
        return repoQuery.getSkus(offeringId, pageRequest);
    }

    public SkuResponse createSku(Sku sku) {
        this.canCreateOrUpdateSku(
                s -> repo.existsByCode(s.getCode()), sku
        );

        return SkuMapper.toSkuResponse(
                repo.save(sku)
        );
    }

    public SkuResponse updateSku(UUID id, Sku sku) {
        this.canCreateOrUpdateSku(
                s -> repo.existsByCodeExcept(id, s.getCode()), sku
        );

        return repo.update(id, sku)
                .map(SkuMapper::toSkuResponse)
                .orElseThrow(() -> new NotFoundException("No such sku found"));
    }

    public void deleteSku(Set<Permissions> permissions, UUID id) {
        if (!SkuPolicy.canDeleteSku(permissions))
            throw new ForbiddenException("Insufficient permissions");

        repo.delete(id);
    }

    public void updateDefaultSkuTier(UUID skuId, long tierId, boolean defaultState) {
        if (defaultState)
            this.canSetDefaultTier(skuId, tierId);

        repo.updateDefaultTier(skuId, tierId, defaultState);
    }
}
