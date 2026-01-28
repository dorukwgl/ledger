package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.catalog.types.DependencyType;
import com.doruk.domain.catalog.types.EnforcedAt;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;

@Entity
public interface SkuDependencies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id();

    DependencyType dependencyType();

    EnforcedAt enforcedAt();

    LocalDateTime createdAt();

    @ManyToOne
    @Column(name = "target_sku_id")
    Skus targetSku();

    @ManyToOne
    @Column(name = "sku_id")
    Skus sku();
}
