package com.doruk.application.policies;

import com.doruk.domain.shared.enums.Permissions;

import java.util.Set;
import java.util.stream.Stream;

public class SysUserPolicy {
    public static boolean canGrantSysAdmin(Set<Permissions> permissions) {
        return permissions.contains(Permissions.DICTATOR_PERMISSION);
    }

    public static boolean canChangeSystemUserRoles(Set<Permissions> permissions) {
        return canGrantSysAdmin(permissions) || permissions.contains(Permissions.MANAGE_SYSTEM_USER_ROLES);
    }

    public static boolean canCreateRole(Set<Permissions> permissions) {
        return Stream.of(Permissions.CREATE_ROLES, Permissions.DICTATOR_PERMISSION)
                .anyMatch(permissions::contains);
    }

    public static boolean canDeleteRole(Set<Permissions> permissions) {
        return canCreateRole(permissions);
    }

    public static boolean canAlterUserAccountStatus(Set<Permissions> permissions) {
        return Stream.of(Permissions.DICTATOR_PERMISSION, Permissions.ALTER_USER_ACCOUNT_STATUS)
                .anyMatch(permissions::contains);
    }

    // check if the given account can be deactivated.
    public static boolean canAccountDeactivated(Set<Permissions> permissions) {
        return !permissions.contains(Permissions.DICTATOR_PERMISSION);
    }

    public static boolean containsNonGrantablePermission(Set<Permissions> permissions) {
        return Stream.of(
                        Permissions.DICTATOR_PERMISSION,
                        Permissions.CREATE_ROLES,
                        Permissions.MANAGE_SYSTEM_USER_ROLES
                )
                .anyMatch(permissions::contains);
    }
}
