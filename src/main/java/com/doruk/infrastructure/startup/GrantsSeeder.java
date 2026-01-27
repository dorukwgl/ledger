package com.doruk.infrastructure.startup;

import com.doruk.infrastructure.persistence.entity.PermissionDraft;
import com.doruk.infrastructure.persistence.entity.RoleDraft;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;

import java.util.List;

@Singleton
@Requires(env = "setup")
@RequiredArgsConstructor
public class GrantsSeeder {
    private final JSqlClient sqlClient;

    public void seedGrants() {
        var ghostPermission = PermissionDraft.$.produce(p -> p.setName("GHOST_PERMISSION"));
        var dictatorPermission = PermissionDraft.$.produce(p -> p.setName("DICTATOR_PERMISSION"));

        var rolePerm = RoleDraft.$.produce(RoleDraft.$.produce(r -> r.setName("GHOST")),
                r -> r.setPermissions(List.of(ghostPermission)));
        var dictatorPerm = RoleDraft.$.produce(RoleDraft.$.produce(
                r -> r.setName("DICTATOR")), r -> r.setPermissions(List.of(dictatorPermission)));

        sqlClient.saveEntitiesCommand(List.of(rolePerm, dictatorPerm))
                .execute();

        System.out.println("Grants seeded...");
    }
}
