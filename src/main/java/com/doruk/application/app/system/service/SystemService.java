package com.doruk.application.app.system.service;

import com.doruk.application.app.system.dto.PermissionResponse;
import com.doruk.application.app.system.dto.RolesResponse;
import com.doruk.application.app.system.dto.UserQuery;
import com.doruk.application.app.system.dto.UserResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.exception.ForbiddenException;
import com.doruk.application.exception.InvalidCredentialException;
import com.doruk.application.exception.InvalidInputException;
import com.doruk.application.interfaces.ObjectStorage;
import com.doruk.application.policies.SysUserPolicy;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.domain.shared.enums.UserAccountStatus;
import com.doruk.infrastructure.persistence.system.SystemRepository;
import com.doruk.infrastructure.startup.PermissionSeeder;
import com.doruk.infrastructure.util.Constants;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Singleton
@RequiredArgsConstructor
public class SystemService {
    private final SystemRepository repo;
    private final ObjectStorage storage;
    private final PermissionSeeder permissionSeeder;

    public PageResponse<UserResponse> getUsers(PageQuery page, UserQuery userQuery) {
        var users = repo.getUsers(page, userQuery);

        return PageResponse.<UserResponse>builder()
                .totalPageCount(users.totalPageCount())
                .totalRowCount(users.totalRowCount())
                .data(users.data().stream().map(u -> u
                        .withProfileIconUrl(storage.resolveUrl(u.profileIcon()))
                        .withProfileIcon(null)).toList())
                .build();
    }

    public RolesResponse getRoles() {
        return repo.getRoles();
    }

    public PermissionResponse getPermissions(String role) {
        return repo.getPermissions(role);
    }

    public void createRole(Set<Permissions> permissions, String role) {
        if (!SysUserPolicy.canCreateRole(permissions))
            throw new ForbiddenException();

        var roleName = role.toUpperCase();

        if (repo.roleExists(roleName))
            throw new ConflictingArgumentException("Role already exists");

        repo.createRole(roleName);
    }

    public void deleteRole(Set<Permissions> permissions, String role) {
        if (!SysUserPolicy.canDeleteRole(permissions))
            throw new ForbiddenException();

        var hasUser = repo.isRoleHasUser(role);
        if (hasUser)
            throw new InvalidCredentialException("Cannot delete the role that has user");
        repo.deleteRole(role);
    }

    public void updateRole(Set<Permissions> permissions, String role, String newRole) {
        if (!SysUserPolicy.canCreateRole(permissions))
            throw new ForbiddenException();

        var roleName = newRole.toUpperCase();
        if (repo.roleExists(roleName))
            throw new ConflictingArgumentException("Cannot update to already existing role");
        repo.updateRole(role.toUpperCase(), roleName);
    }

    public void updateRolePermissions(Set<Permissions> userPermissions, String role, Set<Permissions> payloads) {
        if (!SysUserPolicy.canChangeSystemUserRoles(userPermissions))
            throw new ForbiddenException();

        if (List.of(Constants.SYS_ADMIN_ROLE, Constants.DICTATOR_ROLE).contains(role))
            throw new InvalidInputException("Cannot change the immutable roles");

        if (SysUserPolicy.containsNonGrantablePermission(payloads))
            throw new InvalidInputException("Contains one or more non grantable permissions");

        repo.overrideRolePermissions(role, payloads);
    }

    public void changeUserRole(Set<Permissions> permissions, String userId, Set<String> roles) {
        if (!SysUserPolicy.canChangeSystemUserRoles(permissions))
            throw new ForbiddenException();

        // also check if each roles exists in the system
        if (!repo.rolesExist(roles) || roles.contains(Constants.DICTATOR_ROLE))
            throw new InvalidInputException("Given roles do not exist");

        // only highest privilege can grant sys_admin role
        if (roles.contains(Constants.SYS_ADMIN_ROLE) && !SysUserPolicy.canGrantSysAdmin(permissions))
            throw new ForbiddenException();

        // now check if the given user is sys_admin or dictator
        if (repo.isUserRoleImmutable(userId))
            throw new InvalidInputException("Cannot perform this action on the given user");

        // change the user roles
        repo.changeUserRoles(roles, userId);

        // then logout the user
        repo.removeExistingSessions(userId);
    }

    public void deactivateUserAccount(Set<Permissions> permissions, String userId) {
        if (!SysUserPolicy.canAlterUserAccountStatus(permissions))
            throw new ForbiddenException();

        if (!SysUserPolicy.canAccountDeactivated(permissions))
            throw new InvalidInputException("Given account cannot be deactivated");

        repo.updateUserStatus(userId, UserAccountStatus.INACTIVE);

        // logout user, also remove the biometrics
        repo.removeExistingSessionsAndBiometrics(userId);
    }

    public void activateUserAccount(Set<Permissions> permissions, String userId) {
        if (!SysUserPolicy.canAlterUserAccountStatus(permissions))
            throw new ForbiddenException();
        repo.updateUserStatus(userId, UserAccountStatus.ACTIVE);
    }
}
