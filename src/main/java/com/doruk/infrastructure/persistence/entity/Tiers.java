package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import org.babyfish.jimmer.sql.*;

import java.time.OffsetDateTime;

@Entity
public interface Tiers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String code();

    String name();

    @Column(name = "is_default")
    boolean isDefaults();

    CatalogStatus status();

    OffsetDateTime createdAt();

    OffsetDateTime updatedAt();

    @ManyToOne
    Skus skus();
}
