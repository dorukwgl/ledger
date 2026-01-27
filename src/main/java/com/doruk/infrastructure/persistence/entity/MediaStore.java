package com.doruk.infrastructure.persistence.entity;

import com.doruk.application.enums.ObjectVisibility;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.*;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_store")
public interface MediaStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    String objectKey();

    ObjectVisibility visibility();

    @NotNull
    String mimeType();

    @NotNull
    long size();

    @NotNull
    LocalDateTime createdAt();

    @Nullable
    @LogicalDeleted("now")
    LocalDateTime deletedAt();
}
