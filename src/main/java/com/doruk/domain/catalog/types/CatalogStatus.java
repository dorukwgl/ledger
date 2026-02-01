package com.doruk.domain.catalog.types;

public enum CatalogStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DEPRECATED("deprecated");

    private String name;
    CatalogStatus(String name) {}
}
