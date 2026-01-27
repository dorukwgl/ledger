package com.doruk.infrastructure.startup;

import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.persistence.entity.PermissionDraft;
import com.doruk.infrastructure.persistence.entity.RoleDraft;
import com.doruk.infrastructure.util.Constants;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;

import java.util.Arrays;
import java.util.List;

@Singleton
@Requires(env = "setup")
@RequiredArgsConstructor
public class GrantsSeeder {
    private final JSqlClient sqlClient;

    public void seedGrants() {
        var dictatorPermission = PermissionDraft.$.produce(p -> p.setName("DICTATOR_PERMISSION"));

        var adminPermissions = Arrays.stream(Permissions.values())
                .map(permission -> PermissionDraft.$.produce(p -> p.setName(permission.name())))
                .toList();

        var sysAdminPerm = RoleDraft.$.produce(RoleDraft.$.produce(r -> r.setName(Constants.SYS_ADMIN_ROLE)),
                r -> r.setPermissions(adminPermissions));
        var dictatorPerm = RoleDraft.$.produce(RoleDraft.$.produce(
                r -> r.setName(Constants.DICTATOR_ROLE)), r -> r.setPermissions(List.of(dictatorPermission)));

        sqlClient.saveEntitiesCommand(List.of(sysAdminPerm, dictatorPerm))
                .execute();

        System.out.println("Grants seeded...");
    }
}
