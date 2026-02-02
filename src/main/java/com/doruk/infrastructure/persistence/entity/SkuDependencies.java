package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.catalog.types.DependencyType;
import com.doruk.domain.catalog.types.EnforcedAt;
import org.babyfish.jimmer.sql.*;

import java.time.OffsetDateTime;

@Entity
public interface SkuDependencies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    DependencyType dependencyType();

    EnforcedAt enforcedAt();

    OffsetDateTime createdAt();

    @ManyToOne
    @JoinColumn(name = "target_sku_id")
    Skus targetSku();

    @ManyToOne
    @JoinColumn(name = "sku_id")
    Skus sku();
}
