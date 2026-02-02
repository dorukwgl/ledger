package com.doruk.infrastructure.persistence.users;

import com.doruk.application.app.users.dto.*;
import com.doruk.application.dto.UploadedFile;
import com.doruk.infrastructure.persistence.entity.*;
import com.doruk.infrastructure.persistence.users.mapper.ProfileMapper;
import com.doruk.infrastructure.persistence.users.mapper.UserMapper;
import com.doruk.jooq.tables.UserProfiles;
import com.doruk.jooq.tables.UserRoles;
import com.doruk.jooq.tables.Users;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class UserRepository {
    private final JSqlClient sqlClient;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;
    private final DSLContext dsl;

    public Optional<UserUniqueFields> findByUsernameOrEmail(String username, String email) {
        var t = UserTable.$;
        var userQuery = sqlClient.createQuery(t)
                .where(Predicate.or(t.username().eq(username.toLowerCase(Locale.ROOT)),
                        t.email().eq(email.toLowerCase(Locale.ROOT))))
                .select(t.username(), t.email())
                .execute();

        if (userQuery.isEmpty())
            return Optional.empty();

        var user = userQuery.getFirst();
        return Optional.of(new UserUniqueFields(user.get_1(), user.get_2()));
    }

    public UserResponseDto createUser(CreateUserCmd dto, String hashedPassword) {
        var u = Users.USERS;
        var r = UserRoles.USER_ROLES;
        return dsl.transactionResult(() -> {
            var usr = dsl.insertInto(u)
                    .set(u.USERNAME, dto.username())
                    .set(u.EMAIL, dto.email())
                    .set(u.PASSWORD, hashedPassword)
                    .returning()
                    .fetchOne(rs -> UserResponseDto.builder()
                            .id(rs.getId())
                            .username(rs.getUsername())
                            .email(rs.getEmail())
                            .phone(rs.getPhone())
                            .status(rs.getStatus())
                            .emailVerified(rs.getIsEmailVerified())
                            .phoneVerified(rs.getIsPhoneVerified())
                            .multiFactorAuth(rs.getMultiFactorAuth())
                            .createdAt(rs.getCreatedAt())
                            .updatedAt(rs.getUpdatedAt())
                            .build()
                    );

            dsl.insertInto(r)
                    .set(r.USER_ID, Objects.requireNonNull(usr).id())
                    .set(r.NAME, "USER")
                    .execute();

            return usr;
        });
    }

    public ProfileDto updateProfile(String userId, ProfileDto dto) {
        var draft = UserProfileDraft.$.produce(d -> d
                .setFullName(dto.fullName())
                .setAddress(dto.address())
                .setCity(dto.city())
                .setState(dto.state())
                .setCountry(dto.country())
                .setPostalCode(dto.postalCode())
                .setUpdatedAt(LocalDateTime.now())
                .setUser(UserDraft.$.produce(u ->
                        u.setId(UUID.fromString(userId))))
        );
        var res = sqlClient.saveCommand(draft).setMode(SaveMode.UPSERT).execute();
        return profileMapper.toProfileDto(res.getModifiedEntity());
    }

    public Optional<String> updateProfileIconReturningOld(String userId, UploadedFile icon) {
        return sqlClient.transaction(() -> {
            var t = UserProfileTable.$;
            var oldPic = sqlClient.createQuery(t)
                    .where(t.userId().eq(UUID.fromString(userId)))
                    .select(t.profileIcon())
                    .execute();

            // update with new file id
            sqlClient.createUpdate(t)
                    .where(t.user().id().eq(UUID.fromString(userId)))
                    .set(t.profileIconId(), icon.id())
                    .execute();

            if (oldPic.isEmpty())
                return Optional.empty();

            var oldFile = oldPic.getFirst();
            // delete old file if exists
            sqlClient.deleteById(MediaStore.class, oldFile.id());

            return Optional.of(oldFile.objectKey());
        });
    }

    public CurrentUserDto getCurrentUser(String userId) {
        var u = Users.USERS;
        var r = UserRoles.USER_ROLES;
        var p = UserProfiles.USER_PROFILES;
        return dsl.select(
                        u.ID.cast(String.class),
                        u.USERNAME,
                        u.EMAIL,
                        u.PHONE,
                        u.IS_EMAIL_VERIFIED,
                        u.IS_PHONE_VERIFIED,
                        u.MULTI_FACTOR_AUTH,
                        u.STATUS,
                        u.CREATED_AT,
                        u.UPDATED_AT,

                        DSL.row(
                                p.USER_ID.cast(String.class),
                                p.FULL_NAME,
                                p.ADDRESS,
                                p.CITY,
                                p.STATE,
                                p.COUNTRY,
                                p.POSTAL_CODE,
                                p.CREATED_AT,
                                p.UPDATED_AT
                        ).mapping(ProfileDto::new),
                        // fetch roles
                        DSL.multiset(
                                DSL.select(r.NAME)
                                        .where(r.USER_ID.eq(u.ID))
                        ).convertFrom(rs -> rs.stream().map(Record1::value1).toList())
                )
                .from(u)
                .leftJoin(p).on(u.ID.eq(p.USER_ID))
                .where(u.ID.eq(UUID.fromString(userId)))
                .fetchOne(userMapper::toCurrentUserDto);
    }
}
