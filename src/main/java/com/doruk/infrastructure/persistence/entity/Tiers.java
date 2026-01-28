package com.doruk.infrastructure.persistence.entity;

import java.lang.String;
import java.time.LocalDateTime;

import com.doruk.domain.catalog.types.CatelogStatus;
import org.babyfish.jimmer.sql.*;

@Entity
public interface Tiers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id();

    String code();

    String name();

    boolean isDefault();

    CatelogStatus status();

    LocalDateTime createdAt();

    LocalDateTime updatedAt();

    @ManyToOne
    Skus skus();
}
