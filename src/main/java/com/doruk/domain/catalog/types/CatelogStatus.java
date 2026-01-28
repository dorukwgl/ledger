package com.doruk.domain.catalog.types;

public enum CatelogStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DEPRECATED("deprecated");

    private String name;
    CatelogStatus(String name) {}
}
