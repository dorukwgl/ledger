package com.doruk.infrastructure.startup;

import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.persistence.entity.Permission;
import com.doruk.infrastructure.persistence.entity.PermissionDraft;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;

import java.util.LinkedList;
import java.util.List;

@Singleton
@Requires(env = "setup")
@RequiredArgsConstructor
public class AllPermissionSeeder {
    private final JSqlClient sqlClient;

    public void seedAllPermissions() {
        List<Permission> permissions = new LinkedList<>();
        for (Permissions permission : Permissions.values()) {
            permissions.add(PermissionDraft.$.produce(p ->
            {
                p.setName(permission.name());
                p.setDeletedAt(null);
            }));
        }

        sqlClient.saveEntitiesCommand(permissions)
                .execute();

        System.out.println("All permissions from Permissions enum seeded...");
    }
}
