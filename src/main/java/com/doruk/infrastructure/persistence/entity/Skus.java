package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.SkuCategory;
import com.doruk.domain.catalog.types.SkuScope;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OneToMany;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public interface Skus {
    @Id
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
    OffsetDateTime publishedAt();

    OffsetDateTime createdAt();

    OffsetDateTime updatedAt();

    @ManyToOne
    Offerings offerings();

    @OneToMany(
            mappedBy = "skus"
    )
    List<Tiers> tiers();
}
