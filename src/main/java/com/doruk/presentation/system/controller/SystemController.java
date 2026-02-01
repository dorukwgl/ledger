package com.doruk.presentation.system.controller;

import com.doruk.application.app.system.dto.PermissionResponse;
import com.doruk.application.app.system.dto.RolesResponse;
import com.doruk.application.app.system.dto.UserQuery;
import com.doruk.application.app.system.dto.UserResponse;
import com.doruk.application.app.system.service.SystemService;
import com.doruk.application.dto.PageResponse;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.infrastructure.security.annotation.RequiresPermission;
import com.doruk.presentation.dto.PageQueryMapper;
import com.doruk.presentation.dto.PageQueryRequest;
import com.doruk.presentation.system.dto.UserQueryRequest;
import com.doruk.presentation.utils.AuthUtils;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Tag(name = "System, Users, Roles & Permissions management")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiresPermission(
        value = {
                Permissions.CREATE_ROLES,
                Permissions.MANAGE_SYSTEM_USER_ROLES,
                Permissions.ALTER_USER_ACCOUNT_STATUS
        },
        logical = RequiresPermission.Logical.OR)
@Controller("system")
@RequiredArgsConstructor
public class SystemController {
    private final SystemService service;

    @Operation(description = "returns a sorted pagination list of users with given filters")
    @Get("/")
    public PageResponse<UserResponse> getUsers(UserQueryRequest userQueryRequest, PageQueryRequest pageable) {
        return service.getUsers(PageQueryMapper.toQuery(pageable),
                new UserQuery(userQueryRequest.email()));
    }

    @Operation(description = "Get the list of available user roles")
    @Get("/roles")
    public RolesResponse getRoles() {
        return service.getRoles();
    }

    @Operation(description = "Get the list of available user permissions")
    @Get("/permissions{?role}")
    public PermissionResponse getPermissions(@Nullable String role) {
        return service.getPermissions(role);
    }

    @Operation(description = "Create a new role")
    @Post("/roles")
    public InfoResponse changeUserRoles(Authentication auth, @Body @NotBlank String role) {
        service.createRole(AuthUtils.extractPermissions(auth), role);
        return new InfoResponse("Role added successfully");
    }

    @Operation(description = "Delete the given role")
    @Delete("/roles/{role}")
    public InfoResponse deleteUserRole(Authentication auth, @PathVariable String role) {
        service.deleteRole(AuthUtils.extractPermissions(auth), role);
        return new InfoResponse("Role deleted successfully");
    }

    @Operation(description = "Update or rename the given role into new one")
    @Put("/roles/{role}")
    public InfoResponse updateRole(Authentication auth, @PathVariable String role, @Body @NotBlank String newRole) {
        service.updateRole(AuthUtils.extractPermissions(auth), role, newRole);
        return new InfoResponse("Role updated successfully");
    }

    @Operation(description = "Update or change the current permissions of the given role" +
            "The updated permissions will apply when the user with given role re logs in to the system")
    @Parameter(name = "permissions", description = "List or array of new permissions. " +
            "New permissions can be intersection, subset or superset of the previous permissions." +
            "Note: Given list of permissions will completely overwrite the previous list")
    @Put("/roles/permissions/{role}")
    public InfoResponse changePermissions(Authentication auth, @PathVariable String role, @Body @NotBlank Set<Permissions> permissions) {
        service.updateRolePermissions(AuthUtils.extractPermissions(auth), role, permissions);
        return new InfoResponse("Permissions for given role updated successfully");
    }

    @Operation(description = "Update/Change or remove the user roles. The user is logged out immediately. " +
            "Changes will take effect when the user re logs in.")
    @Parameter(name = "roles", description = "List of new roles for the given user. " +
            "It can be intersection, superset or subset of previous roles. " +
            "Note: new roles will completely overwrite the previous list.")
    @Put("/users/roles/{userId}")
    public InfoResponse changeUserRoles(Authentication auth, @PathVariable String userId, @Body @NotBlank Set<String> roles) {
        service.changeUserRole(AuthUtils.extractPermissions(auth), userId, roles);
        return new InfoResponse("Roles for given user updated successfully");
    }

    @Operation(description = "Deactivate the user account. User is immediately logged out. " +
            "Note: Higher privilege users account cannot be deactivated.")
    @Put("/users/deactivate/{userId}")
    public InfoResponse deactivateUser(Authentication auth, @PathVariable @NotBlank String userId) {
        service.deactivateUserAccount(AuthUtils.extractPermissions(auth), userId);
        return new InfoResponse("User account deactivated successfully");
    }

    @Operation(description = "Activate or Reactivate the previously deactivated user accounts")
    @Put("/users/activate/{userId}")
    public InfoResponse activateUser(Authentication auth, @PathVariable @NotBlank String userId) {
        service.activateUserAccount(AuthUtils.extractPermissions(auth), userId);
        return new InfoResponse("User account activated successfully");
    }
}
