package com.doruk.application.policies;

import com.doruk.domain.shared.enums.Permissions;

import java.util.Set;

public class ProductPolicy {
    public static boolean canDeleteProduct(Set<Permissions> permissions) {
        return permissions.contains(Permissions.DELETE_CATALOG_PRODUCTS);
    }
}
