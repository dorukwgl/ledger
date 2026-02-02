package com.doruk.infrastructure.persistence.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.OffsetDateTime;
import java.util.List;

@Introspected
@Entity
@Table(name = "sessions")
public interface Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user();

    @Key
    String sessionId();

    @Nullable
    String deviceInfo();

    @Nullable
    @Key
    String deviceId();

    @Column(name = "expires_at")
    OffsetDateTime expiresAt();

    @Column(name = "created_at")
    OffsetDateTime createdAt();

    @Column(name = "permissions")
    List<Integer> cachedPermissions();
}
