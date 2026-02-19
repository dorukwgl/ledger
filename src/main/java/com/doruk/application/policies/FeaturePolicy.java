package com.doruk.application.policies;

import java.util.Set;

import com.doruk.domain.shared.enums.Permissions;

public class FeaturePolicy {
    public static boolean canDeleteFeature(Set<Permissions> permissions) {
        return permissions.contains(Permissions.DELETE_CATALOG_PRODUCTS);
    }
}
