package com.doruk.infrastructure.persistence.entity;

import java.lang.String;
import java.time.LocalDateTime;

import com.doruk.domain.catalog.types.CatalogStatus;
import org.babyfish.jimmer.sql.*;

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

    LocalDateTime createdAt();

    LocalDateTime updatedAt();

    @ManyToOne
    Skus skus();
}
