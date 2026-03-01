package com.doruk.domain.catalog.TierFeature.entity;

import com.doruk.domain.catalog.types.FeatureValue;
import com.doruk.domain.exception.InvalidDataShapeException;
import com.doruk.infrastructure.util.StringUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TierFeature {
    private Long id;
    private long tierId;
    private long featureId;
    private FeatureValue valueType;
    private String value;

    private static void validateValueType(FeatureValue valueType, String value) {
        var valid = switch (valueType) {
            case BOOLEAN -> value.equals("true") || value.equals("false");
            case NUMBER -> StringUtil.isNumber(value);
            case STRING -> true;
        };

        if (!valid)
            throw new InvalidDataShapeException("Invalid data shape. Expected " + valueType.name() + ", found " + value);
    }

    public static TierFeature create(long tierId, long featureId, FeatureValue valueType, String value) {
        validateValueType(valueType, value);
        return new TierFeature(null, tierId, featureId, valueType, value);
    }

    public static TierFeature rehydrate(long id, long tierId, long featureId, String value) {
        return  new TierFeature(id, tierId, featureId, null, value);
    }
}
