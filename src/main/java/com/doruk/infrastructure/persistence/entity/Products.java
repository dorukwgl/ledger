package com.doruk.infrastructure.persistence.entity;

import java.lang.String;
import java.time.LocalDateTime;

import com.doruk.domain.catalog.types.Ownership;
import com.doruk.domain.catalog.types.ProductKind;
import com.doruk.domain.catalog.types.CatelogStatus;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.jetbrains.annotations.Nullable;

@Entity
public interface Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id();

    String code();

    String name();

    @Nullable
    String description();

    ProductKind productKind();

    Ownership ownership();

    CatelogStatus status();

    LocalDateTime createdAt();

    LocalDateTime updatedAt();
}
