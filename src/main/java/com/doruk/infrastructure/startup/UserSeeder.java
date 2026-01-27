package com.doruk.infrastructure.startup;

import com.doruk.application.security.PasswordEncoder;
import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.infrastructure.persistence.entity.RoleDraft;
import com.doruk.infrastructure.persistence.entity.UserDraft;
import com.doruk.infrastructure.util.Constants;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.LogicalDeletedBehavior;

import java.util.List;

@Singleton
@Requires(env = "setup")
@RequiredArgsConstructor
public class UserSeeder {
    private final JSqlClient client;
    private final PasswordEncoder hasher;

    public void seedUsers() {
        var dictator = UserDraft.$.produce(u -> u.setUsername("dorukwgl")
                .setEmail("dorukwagle@gmail.com")
                .setEmailVerified(true)
                .setPhoneVerified(true)
                .setPhone("9829293466")
                .setRoles(List.of(RoleDraft.$.produce(r -> r.setName(Constants.DICTATOR_ROLE))))
                .setMultiFactorAuth(MultiAuthType.EMAIL)
                .setPassword(hasher.encode("dorukwgl@ihbibicdff"))
        );

        var sysAdmin = UserDraft.$.produce(u -> u.setUsername("doruk")
                .setEmail("chrishdev.chd@gmail.com")
                .setEmailVerified(true)
                .setPhoneVerified(true)
                .setPhone("9829293466")
                .setRoles(List.of(RoleDraft.$.produce(r -> r.setName(Constants.SYS_ADMIN_ROLE))))
                .setMultiFactorAuth(MultiAuthType.NONE)
                .setPassword(hasher.encode("chd@ihbibicdff"))
        );

        var user = UserDraft.$.produce(u -> u.setUsername("testuser")
                .setEmail("user@gmail.com")
                .setPhoneVerified(true)
                .setPhoneVerified(true)
                .setPhone("9829293466")
                .setMultiFactorAuth(MultiAuthType.NONE)
                .setRoles(List.of(RoleDraft.$.produce(r -> r.setName("USER"))))
                .setPassword(hasher.encode("password"))
        );

        client
                .filters(c -> c.setBehavior(LogicalDeletedBehavior.IGNORED))
                .saveEntitiesCommand(List.of(dictator, sysAdmin, user))
                .execute();

        System.out.println("Users seeded...");
    }
}
