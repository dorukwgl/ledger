package com.doruk.infrastructure.persistence.entity;

import java.lang.Integer;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.doruk.domain.catalog.types.SkuCategory;
import com.doruk.domain.catalog.types.SkuScope;
import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.infrastructure.util.V7Generator;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

@Entity
public interface Skus {
    @Id
    @GeneratedValue(generatorType = V7Generator.class)
    UUID id();

    String code();

    String name();

    SkuCategory category();

    SkuScope scope();

    boolean isFreemium();

    @Nullable
    Integer trialDays();

    @Nullable
    Integer termHintMonths();

    CatalogStatus status();

    @Nullable
    LocalDateTime publishedAt();

    LocalDateTime createdAt();

    LocalDateTime updatedAt();

    @ManyToOne
    Offerings offerings();

    @OneToMany(
            mappedBy = "skus"
    )
    List<Tiers> tiers();
}
