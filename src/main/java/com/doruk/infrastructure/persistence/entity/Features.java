package com.doruk.infrastructure.persistence.entity;

import java.lang.String;
import java.time.LocalDateTime;
import java.util.List;

import com.doruk.domain.catalog.types.FeatureValue;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

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

    LocalDateTime createdAt();

    @OneToMany(
            mappedBy = "features"
    )
    List<TierFeatures> tierFeatures();
}
