package com.doruk.infrastructure.persistence.entity;

import com.doruk.infrastructure.util.V7Generator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "biometrics")
public interface Biometric {
    @Id
    @GeneratedValue(generatorType = V7Generator.class)
    UUID id();

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user();

    @Key
    byte[] publicKey();

    @Key
    String deviceId();

    @Nullable
    LocalDateTime lastUsedAt();
}
