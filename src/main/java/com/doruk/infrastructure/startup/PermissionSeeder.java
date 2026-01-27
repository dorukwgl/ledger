package com.doruk.infrastructure.startup;

import com.doruk.infrastructure.persistence.entity.PermissionDraft;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Singleton
@Requires(env = "setup")
@RequiredArgsConstructor
public class PermissionSeeder {
    private final JSqlClient sqlClient;

    public void seedPermissions() {
        var permission = PermissionDraft.$.produce(p -> p.setName("DICTATOR_PERMISSION").setDeletedAt(LocalDateTime.now()));
        sqlClient.saveCommand(permission).execute();

        System.out.println("Permissions seeded...");
    }
}
