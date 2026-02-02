package com.doruk.infrastructure.persistence.entity;

import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "biometrics")
public interface Biometric {
    @Id
    UUID id();

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user();

    @Key
    byte[] publicKey();

    @Key
    String deviceId();

    @Nullable
    OffsetDateTime lastUsedAt();
}
