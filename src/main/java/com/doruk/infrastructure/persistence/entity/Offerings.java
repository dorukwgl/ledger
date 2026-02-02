package com.doruk.infrastructure.persistence.entity;

import com.doruk.domain.catalog.types.CatalogStatus;
import com.doruk.domain.catalog.types.DeliveryModel;
import com.doruk.domain.catalog.types.InfraModelType;
import com.doruk.domain.catalog.types.OperationalOwner;
import org.babyfish.jimmer.sql.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
public interface Offerings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String code();

    String name();

    DeliveryModel deliveryModel();

    InfraModelType infraModel();

    OperationalOwner operationalOwner();

    CatalogStatus status();

    OffsetDateTime createdAt();

    OffsetDateTime updatedAt();

    @ManyToOne
    Products products();

    @OneToMany(
            mappedBy = "offerings"
    )
    List<Skus> skus();
}
