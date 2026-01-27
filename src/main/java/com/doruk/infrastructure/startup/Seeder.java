package com.doruk.infrastructure.startup;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@Requires(env = "setup")
@RequiredArgsConstructor
public class Seeder {
    private final RolesSeeder rolesSeeder;
    private final GrantsSeeder grantsSeeder;
    private final PermissionSeeder permissionSeeder;
    private final AllPermissionSeeder allPermissionSeeder;
    private final UserSeeder userSeeder;

    @EventListener
    public void seed(StartupEvent e) {
        allPermissionSeeder.seedAllPermissions();
        rolesSeeder.seedRoles();
        permissionSeeder.seedPermissions();
        grantsSeeder.seedGrants();
        userSeeder.seedUsers();

        System.out.println("Seeding Completed...");
    }
}
