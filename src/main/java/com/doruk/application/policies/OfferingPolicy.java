package com.doruk.application.policies;

import com.doruk.domain.shared.enums.Permissions;

import java.util.Set;

public class OfferingPolicy {
    public static boolean canDeleteOffering(Set<Permissions> permissions) {
        return permissions.contains(Permissions.DELETE_CATALOG_PRODUCTS);
    }
}
