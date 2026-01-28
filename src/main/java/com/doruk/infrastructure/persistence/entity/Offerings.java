package com.doruk.infrastructure.persistence.entity;

import java.lang.String;
import java.time.LocalDateTime;
import java.util.List;

import com.doruk.domain.catalog.types.DeliveryModel;
import com.doruk.domain.catalog.types.InfraModelType;
import com.doruk.domain.catalog.types.OperationalOwner;
import com.doruk.domain.catalog.types.CatelogStatus;
import org.babyfish.jimmer.sql.*;

@Entity
public interface Offerings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id();

    String code();

    String name();

    DeliveryModel deliveryModel();

    InfraModelType infraModel();

    OperationalOwner operationalOwner();

    CatelogStatus status();

    LocalDateTime createdAt();

    LocalDateTime updatedAt();

    @ManyToOne
    Products products();

    @OneToMany(
            mappedBy = "offerings"
    )
    List<Skus> skus();
}
