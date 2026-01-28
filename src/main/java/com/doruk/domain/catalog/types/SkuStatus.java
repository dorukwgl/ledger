package com.doruk.domain.catalog.types;

public enum SkuStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DEPRECATED("deprecated");

    private String name;
    SkuStatus(String name) {}
}
