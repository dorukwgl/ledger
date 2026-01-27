package com.doruk.domain.shared.enums;

import com.doruk.domain.exception.DomainException;

public enum Permissions {
    DICTATOR_PERMISSION(0),
    MANAGE_SYSTEM_USER_ROLES(1),
    CREATE_ROLES (3),
    ALTER_USER_ACCOUNT_STATUS(4),
    UPDATE_OWN_PROFILE (5);

    private final int value;

    Permissions(int value) {
        this.value = value;
    }

    public int id() {
        return this.value;
    }

    public static Permissions fromId(int id) {
        for (Permissions permission : Permissions.values()) {
            if (permission.id() == id) {
                return permission;
            }
        }
        throw new DomainException("Invalid permission id: " + id);
    }
}
