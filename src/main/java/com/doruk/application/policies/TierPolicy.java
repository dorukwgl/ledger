package com.doruk.application.policies;

import java.util.Set;

import com.doruk.domain.shared.enums.Permissions;

public class TierPolicy {
    public static boolean canDeleteTier(Set<Permissions> permissions) {
        return permissions.contains(Permissions.DELETE_CATALOG_PRODUCTS);
    }
}
