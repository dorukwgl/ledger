package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.domain.shared.enums.UserAccountStatus;
import com.doruk.infrastructure.util.V7Generator;
import io.micronaut.core.annotation.Introspected;
import jdk.jfr.Timestamp;
import org.babyfish.jimmer.sql.*;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Introspected
@Entity
@Table(name = "users")
public interface User {
    @Id
    @GeneratedValue(generatorType = V7Generator.class)
    UUID id();

    @Key
    String username();

    @Key
    String email();

    @Nullable
    String phone();

    String password();

    // SQL defaults to 'NONE', mapped via Enum name
    @Column(name = "multi_factor_auth")
    MultiAuthType multiFactorAuth();

    @Column(name = "is_email_verified")
    boolean emailVerified();

    @Column(name = "is_phone_verified")
    boolean phoneVerified();

    @Column(name = "status")
    UserAccountStatus status();

    @Nullable
    @Column(name = "updated_at")
    LocalDateTime updatedAt();

    @Nullable
    LocalDateTime createdAt();

    // --- Relationships ---

    @OneToOne(mappedBy = "user")
    @Nullable
    UserProfile profile();

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumnName = "user_id",
            inverseJoinColumnName = "name"
    )
    List<Role> roles();

    @OneToMany(mappedBy = "user")
    List<Session> sessions();

    @OneToMany(mappedBy = "user")
    List<Biometric> biometrics();
}
