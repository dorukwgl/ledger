package com.doruk.application.app.catalog.skus;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import com.doruk.application.app.catalog.skus.dto.TierResponse;
import com.doruk.application.app.catalog.skus.mapper.TierMapper;
import com.doruk.application.app.catalog.skus.repository.TierQueryRepository;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.exception.ForbiddenException;
import com.doruk.application.exception.InvalidInputException;
import com.doruk.application.exception.NotFoundException;
import com.doruk.application.policies.TierPolicy;
import com.doruk.domain.catalog.skus.entity.Tier;
import com.doruk.domain.catalog.skus.repository.TierRepository;
import com.doruk.domain.shared.enums.Permissions;

@Singleton
@RequiredArgsConstructor
public class TierService {
    private final TierRepository repo;
    private final TierQueryRepository repoQuery;
    
    private void canCreateOrUpdateTier(Predicate<Tier> tierExist, Tier tier) {
        var tierExists = tierExist.test(tier);
        
        // NULL check for updates, where sku id can be null
        var skuExists = Optional.of(tier.getSkuId())
                .map(repo::skuExists)
                .orElse(true);

        if (!skuExists)
            throw new InvalidInputException("Sku not found");
        
        if (tierExists)
            throw new ConflictingArgumentException("Tier code already exists");
    }

    public PageResponse<TierResponse> getTiers(UUID skuId, PageQuery pageRequest) {
        return repoQuery.getTiers(skuId, pageRequest);
    }

    public TierResponse createTier(Tier tier) {
        this.canCreateOrUpdateTier(
                t -> repo.existsByCode(t.getCode()), tier
        );

        return TierMapper.toTierResponse(
                repo.save(tier)
        );
    }

    public TierResponse updateTier(long id, Tier tier) {
        this.canCreateOrUpdateTier(
                t -> repo.existsByCodeExcept(id, t.getCode()), tier
        );

        return repo.update(id, tier)
                .map(TierMapper::toTierResponse)
                .orElseThrow(() -> new NotFoundException("No such tier found"));
    }

    public void deleteTier(Set<Permissions> permissions, long id) {
        if (!TierPolicy.canDeleteTier(permissions))
            throw new ForbiddenException("Insufficient permissions");
        
        repo.delete(id);
    }
}
