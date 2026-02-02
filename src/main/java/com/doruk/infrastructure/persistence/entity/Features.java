package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.catalog.types.FeatureValue;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
public interface Features {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String code();

    String name();

    @Nullable
    String description();

    FeatureValue valueType();

    @Nullable
    String unit();

    OffsetDateTime createdAt();

    @OneToMany(
            mappedBy = "features"
    )
    List<TierFeatures> tierFeatures();
}
