package com.doruk.infrastructure.persistence.entity;

import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public interface UserProfile {
    // The ID matches the User ID exactly
    @Id
    UUID id();

    // The relationship to User
    @OneToOne
    @JoinColumn(name = "user_id")
    User user();

    @Nullable
    String fullName();

    @OneToOne
    @JoinColumn(name = "profile_icon")
    MediaStore profileIcon();

    // Address fields...
    @Nullable
    String address();

    @Nullable
    String city();

    @Nullable
    String state();

    @Nullable
    String country();

    @Column(name = "postal_code")
    @Nullable
    String postalCode();

    LocalDateTime createdAt();

    LocalDateTime updatedAt();
}