package com.doruk.infrastructure.persistence.entity;

import java.lang.String;

import org.babyfish.jimmer.sql.*;

@Entity
public interface TierFeatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long tid();

    String value();

    @ManyToOne
    Features features();

    @ManyToOne
    Tiers tiers();
}
