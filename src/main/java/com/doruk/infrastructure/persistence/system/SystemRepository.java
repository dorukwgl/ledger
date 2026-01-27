package com.doruk.infrastructure.persistence.system;

import com.doruk.application.app.system.dto.PermissionResponse;
import com.doruk.application.app.system.dto.RolesResponse;
import com.doruk.application.app.system.dto.UserQuery;
import com.doruk.application.app.system.dto.UserResponse;
import com.doruk.application.dto.PageQuery;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.enums.SortOrder;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.domain.shared.enums.UserAccountStatus;
import com.doruk.infrastructure.persistence.entity.*;
import com.doruk.infrastructure.persistence.system.mapper.UserResponseMapper;
import com.doruk.infrastructure.util.Constants;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.LikeMode;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode;
import org.babyfish.jimmer.sql.ast.mutation.DeleteMode;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class SystemRepository {
    private final JSqlClient client;

    public PageResponse<UserResponse> getUsers(PageQuery page, UserQuery query) {
        var t = UserTable.$;
        var res = client.createQuery(t)
                .where(t.email().ilikeIf(query.email(), LikeMode.ANYWHERE))
                .orderBy(page.order() == SortOrder.ASC ? t.id().asc() : t.id().desc())
                .select(t.fetch(
                        UserFetcher.$
                                .username()
                                .email()
                                .roles()
                                .roles(RoleFetcher.$.permissions())
                                .profile(UserProfileFetcher.$.profileIcon(MediaStoreFetcher.$.allScalarFields()))
                ))
                .fetchPage(page.page(), page.size());

        return UserResponseMapper.toUserPageResponse(res);
    }

    public RolesResponse getRoles() {
        var roles = client.createQuery(RoleTable.$)
                .select(RoleTable.$.name())
                .execute();

        return new RolesResponse(roles);
    }

    public PermissionResponse getPermissions(String role) {
        var t = PermissionTableEx.$;
        var permissions = client.createQuery(t)
                .where(t.roles().name().eqIf(role))
                .select(t.name())
                .execute();

        return new PermissionResponse(permissions);
    }

    public void createRole(String role) {
        var draft = RoleDraft.$.produce(d -> d.setName(role));
        client.saveCommand(draft)
                .setMode(SaveMode.UPSERT)
                .execute();
    }

    public void updateRole(String role, String newRole) {
        var t = RoleTable.$;
        client.createUpdate(t)
                .where(t.name().eq(role))
                .set(t.name(), newRole)
                .execute();
    }

    public void deleteRole(String role) {
        client.createDelete(RoleTable.$)
                .setMode(DeleteMode.PHYSICAL)
                .where(RoleTable.$.name().eq(role))
                .execute();
    }

    public boolean isRoleHasUser(String role) {
        var t = UserTableEx.$;
        return client.createQuery(t)
                .where(t.roles().name().eq(role))
                .exists();
    }

    public void overrideRolePermissions(String role, Set<Permissions> permissions) {
        var draft = RoleDraft.$.produce(r -> r.setName(role)
                .setPermissions(permissions.stream().map(Permissions::name).map(p ->
                        PermissionDraft.$.produce(d -> d.setName(p))).toList()));

        // delete old associations and insert new ones
        client.saveCommand(draft).setAssociatedMode(RoleTable.PERMISSIONS, AssociatedSaveMode.REPLACE).execute();
    }

    public void changeUserRoles(Set<String> roles, String userId) {
        var draft = UserDraft.$.produce(u -> u.setId(UUID.fromString(userId))
                .setRoles(roles.stream().map(r -> RoleDraft.$.produce(d -> d.setName(r))).toList()));

        client.saveCommand(draft).setAssociatedMode(UserTable.ROLES, AssociatedSaveMode.REPLACE).execute();
    }

    public void removeExistingSessions(String userId) {
        var t = SessionTable.$;
        client.createDelete(t)
                .where(t.userId().eq(UUID.fromString(userId)))
                .execute();
    }

    public void removeExistingSessionsAndBiometrics(String userId) {
        this.removeExistingSessions(userId);

        var t = BiometricTable.$;
        client.createDelete(t)
                .where(t.userId().eq(UUID.fromString(userId)))
                .execute();
    }

    public void updateUserStatus(String userId, UserAccountStatus status) {
        var t = UserTable.$;
        client.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.status(), status)
                .execute();
    }

    public boolean roleExists(String role) {
        var t = RoleTable.$;
        return client.createQuery(t)
                .where(t.name().eq(role))
                .exists();
    }

    public boolean rolesExist(Set<String> roles) {
        var t = RoleTable.$;
        var result = client.createQuery(t)
                .where(t.name().in(roles))
                .selectCount()
                .execute()
                .getFirst();

        return result == roles.size();
    }

    public boolean isUserRoleImmutable(String userId) {
        var t = UserTableEx.$;
        return client.createQuery(t)
                .where(Predicate.and(
                                t.id().eq(UUID.fromString(userId))),
                        t.roles(JoinType.INNER).name()
                                .in(List.of(Constants.SYS_ADMIN_ROLE, Constants.DICTATOR_ROLE))
                )
                .exists();
    }

//    public boolean isUserSystemAdmin(String userId) {
//        var t = UserTableEx.$;
//        return client.createQuery(t)
//                .where(t.id().eq(UUID.fromString(userId)))
//                .where(t.roles().name().eq(Constants.SYS_ADMIN_ROLE))
//                .exists();
//    }
//
//    public boolean isUserDictator(String userId) {
//        var  t = UserTableEx.$;
//        return client.createQuery(t)
//                .where(t.id().eq(UUID.fromString(userId)))
//                .where(t.roles().name().eq(Constants.DICTATOR))
//                .exists();
//    }
}
