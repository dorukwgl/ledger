package com.doruk.application.app.catalog.tierfeature;

import com.doruk.application.app.catalog.tierfeature.dto.FeatureResponse;
import com.doruk.application.app.catalog.tierfeature.repository.TierFeatureServiceRepo;
import com.doruk.application.exception.InvalidInputException;
import com.doruk.domain.catalog.TierFeature.entity.TierFeature;
import com.doruk.domain.catalog.TierFeature.repository.TierFeatureRepository;
import io.micronaut.http.client.loadbalance.ServiceInstanceListLoadBalancerFactory;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Singleton
@RequiredArgsConstructor
public class TierFeatureService {
    private final TierFeatureRepository domainRepo;
    private final TierFeatureServiceRepo serviceRepo;
    private final ServiceInstanceListLoadBalancerFactory serviceInstanceListLoadBalancerFactory;

    private void checkTierFeatureExistence(long tierId, long featureId) {
        if (!serviceRepo.tierExists(tierId))
            throw new InvalidInputException("No such tier found");

        if (!serviceRepo.featureExists(featureId))
            throw new InvalidInputException("No such feature found");
    }

    public List<FeatureResponse> getFeatures(long tierId) {
        return serviceRepo.getFeatures(tierId);
    }

    public FeatureResponse createTierFeature(long tierId, long featureId, String value) {
        checkTierFeatureExistence(tierId, featureId);
        var valueType = serviceRepo.getFeatureValue(featureId);

        var tierFeature = TierFeature.create(tierId, featureId, valueType, value);

        var tf = domainRepo.save(tierFeature);

        return serviceRepo.getFeature(tf.getId());
    }

    public FeatureResponse updateTierFeature(long tierId, long featureId, String value) {
        if (!serviceRepo.tierFeatureExists(tierId, featureId))
            throw new InvalidInputException("No such tier found");

        var valueType = serviceRepo.getFeatureValue(featureId);

        var tierFeature = TierFeature.create(tierId, featureId, valueType, value);
        var tf = domainRepo.update(tierFeature);

        return tf.map(
                f -> serviceRepo.getFeature(f.getId())
        ).orElseThrow(() -> new InvalidInputException("No such feature found"));
    }

    public void deleteTierFeature(long tid) {
        serviceRepo.delete(tid);
    }
}
