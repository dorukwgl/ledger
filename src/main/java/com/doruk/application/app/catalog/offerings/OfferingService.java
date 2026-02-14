package com.doruk.application.app.catalog.offerings;

import com.doruk.application.app.catalog.offerings.dto.OfferingResponse;
import com.doruk.application.app.catalog.offerings.mapper.OfferingMapper;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.exception.ForbiddenException;
import com.doruk.application.exception.InvalidInputException;
import com.doruk.application.exception.NotFoundException;
import com.doruk.application.policies.OfferingPolicy;
import com.doruk.domain.catalog.offerings.entity.Offering;
import com.doruk.domain.catalog.offerings.repository.OfferingRepository;
import com.doruk.domain.shared.enums.Permissions;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Singleton
@RequiredArgsConstructor
public class OfferingService {
    private final OfferingRepository repo;

    private void canCreateOrUpdateOffering(Predicate<Offering> offeringExist, Offering offering) {
        var offerExists = offeringExist.test(offering);
        // null check for updates, where product id can be null
        var proExists = Optional.ofNullable(offering.getProductId())
                .map(repo::productExists)
                .orElse(true);

        if (offerExists)
            throw new ConflictingArgumentException("Product offering code already exists");

        if (!proExists)
            throw new InvalidInputException("Product not found");
    }

    public OfferingResponse getOffering(String code) {
        return repo.getOffering(code)
                .orElseThrow(NotFoundException::new);
    }

    public PageResponse<OfferingResponse> getAllOfferings(@Nullable Long productId, @Nullable String offeringCodeSearch, PageQuery pageQuery) {
        return repo.getOfferings(productId, offeringCodeSearch, pageQuery);
    }

    public OfferingResponse createOffering(Offering offering) {
        this.canCreateOrUpdateOffering(
                o -> repo.offeringExists(o.getCode()), offering
        );

        return OfferingMapper.toOfferingResponse(
                repo.save(offering)
        );
    }

    public OfferingResponse updateOffering(long id, Offering offering) {
        this.canCreateOrUpdateOffering(o ->
                repo.offeringExistsExcept(o.getId(), o.getCode()), offering);

        return repo.updateOffering(id, offering)
                .map(OfferingMapper::toOfferingResponse)
                .orElseThrow(() -> new NotFoundException("No such offering found"));
    }

    public void deleteOffering(Set<Permissions> permissions, long id) {
        if (!OfferingPolicy.canDeleteOffering(permissions))
            throw new ForbiddenException("Insufficient permissions");

        repo.deleteOffering(id);
    }
}
