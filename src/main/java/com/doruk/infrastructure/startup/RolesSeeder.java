package com.doruk.infrastructure.startup;

import com.doruk.infrastructure.persistence.entity.Role;
import com.doruk.infrastructure.persistence.entity.RoleDraft;
import com.doruk.infrastructure.util.Constants;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;

import java.time.LocalDateTime;
import java.util.List;

@Singleton
@Requires(env = "setup")
@RequiredArgsConstructor
public class RolesSeeder {
    private final JSqlClient client;

    public void seedRoles() {
        Role role2 = RoleDraft.$.produce(r -> r.setName(Constants.DICTATOR_ROLE).setDeletedAt(LocalDateTime.now()));
        Role role3 = RoleDraft.$.produce(r -> r.setName(Constants.SYS_ADMIN_ROLE).setDeletedAt(null));
        Role role4 = RoleDraft.$.produce(r -> r.setName("USER").setDeletedAt(null));
        client.saveEntitiesCommand(List.of(role2, role3, role4))
                .execute();

        System.out.println("Roles seeded...");
    }
}
