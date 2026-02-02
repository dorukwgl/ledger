package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.Ownership;
import com.doruk.domain.catalog.types.ProductKind;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

@Entity
public interface Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String code();

    String name();

    @Nullable
    String description();

    ProductKind productKind();

    Ownership ownership();

    CatalogStatus status();

    OffsetDateTime createdAt();

    OffsetDateTime updatedAt();
}
