package com.doruk.infrastructure.persistence.entity;

import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "roles")
public interface Role {
    @Id
    String name();

    @ManyToMany(mappedBy = "roles")
    List<User> users();

    @ManyToMany
    @JoinTable(
            name = "role_permissions",
            joinColumnName = "role_name",
            inverseJoinColumnName = "permission_name"
    )
    List<Permission> permissions();

    @LogicalDeleted("now")
    @Nullable
//    @Column(name = "deleted_at")
    LocalDateTime deletedAt();
}
