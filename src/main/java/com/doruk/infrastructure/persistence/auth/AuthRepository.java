package com.doruk.infrastructure.persistence.auth;

import com.doruk.application.app.auth.dto.AuthDto;
import com.doruk.application.app.auth.dto.BiometricDto;
import com.doruk.application.app.auth.dto.SessionDto;
import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.persistence.auth.mapper.BiometricMapper;
import com.doruk.infrastructure.persistence.auth.mapper.SessionMapper;
import com.doruk.infrastructure.persistence.entity.*;
import com.doruk.infrastructure.util.Constants;
import com.doruk.jooq.tables.Biometrics;
import com.doruk.jooq.tables.RolePermissions;
import com.doruk.jooq.tables.UserRoles;
import com.doruk.jooq.tables.Users;
import jakarta.inject.Singleton;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.*;

@Singleton
@RequiredArgsConstructor
public class AuthRepository {
    private final JSqlClient sqlClient;
    private final SessionMapper sessionMapper;
    private final BiometricMapper biometricMapper;
    private final DSLContext dsl;

    private String getDeviceId(String sessionId) {
        return sqlClient.createQuery(SessionTable.$)
                .where(SessionTable.$.sessionId().eq(sessionId))
                .select(SessionTable.$.deviceId())
                .execute()
                .getFirst();
    }

    private Optional<AuthDto> findUserWithPermissions(org.jooq.Condition whereCondition) {
        var u = Users.USERS;
        var r = UserRoles.USER_ROLES;
        var p = RolePermissions.ROLE_PERMISSIONS;
        var rec = dsl.select(
                        u.ID,
                        u.USERNAME,
                        u.PASSWORD,
                        u.PHONE,
                        u.EMAIL,
                        u.IS_EMAIL_VERIFIED,
                        u.IS_PHONE_VERIFIED,
                        u.MULTI_FACTOR_AUTH,
                        // fetch list of permissions
                        DSL.multiset(
                                dsl.selectDistinct(p.PERMISSION_NAME)
                                        .from(r)
                                        .join(p).on(p.ROLE_NAME.eq(r.NAME))
                                        .where(r.USER_ID.eq(u.ID))
                        ).convertFrom(rs -> new HashSet<>(rs.map(perm -> Permissions.valueOf(perm.value1()))))
                )
                .from(u)
                .where(whereCondition)
                .fetchOne(rs -> AuthDto.builder()
                        .id(rs.value1().toString())
                        .username(rs.value2())
                        .password(rs.value3())
                        .phone(rs.value4())
                        .email(rs.value5())
                        .emailVerified(rs.value6())
                        .phoneVerified(rs.value7())
                        .multiFactorAuth(rs.value8())
                        .permissions(rs.value9())
                        .build()
                );

        return Optional.ofNullable(rec);
    }

    public Optional<AuthDto> findByUsernameOrEmail(String field) {
        var u = Users.USERS;
        return this.findUserWithPermissions(u.USERNAME.eq(field).or(u.EMAIL.eq(field)));
    }

    public Optional<AuthDto> findByUserId(String userId) {
        var u = Users.USERS;
        return this.findUserWithPermissions(u.USERNAME.eq(userId));
    }

    public void createSession(String userId,
                              String sessionId,
                              Set<Permissions> permissions,
                              OffsetDateTime expiration,
                              Optional<String> deviceId,
                              Optional<String> deviceInfo) {

        sqlClient.saveCommand(SessionDraft.$.produce(s ->
                s.setUserId(UUID.fromString(userId))
                        .setSessionId(sessionId)
                        .setCachedPermissions(permissions.stream().map(Permissions::id).toList())
                        .setExpiresAt(expiration)
                        .setDeviceId(deviceId.orElse(null))
                        .setDeviceInfo(deviceInfo.orElse(null)))
        ).execute();
    }

    public Optional<SessionDto> getActiveSession(String sessionId) {
        var t = SessionTable.$;
        return sqlClient.createQuery(t)
                .where(Predicate.and(t.sessionId().eq(sessionId),
                        t.expiresAt().gt(OffsetDateTime.now())))
                .select(t)
                .execute()
                .stream()
                .map(sessionMapper::toDto)
                .findFirst();
    }

    public List<SessionDto> getActiveDevices(String userId) {
        var t = SessionTable.$;
        return sqlClient.createQuery(t)
                .where(Predicate.and(
                                t.userId().eq(UUID.fromString(userId))),
                        t.expiresAt().gt(OffsetDateTime.now())
                )
                .select(t.id(), t.deviceId(), t.deviceInfo(), t.createdAt())
                .execute()
                .stream()
                .map(s -> SessionDto.builder()
                        .id(s.get_1().toString())
                        .deviceId(s.get_2())
                        .deviceInfo(s.get_3())
                        .createdAt(s.get_4())
                        .build())
                .toList();
    }

    public void deleteSession(String sessionId) {
        var t = SessionTable.$;
        sqlClient.createDelete(t)
                .where(t.sessionId().eq(sessionId))
                .execute();
    }

    public void deleteAllSessions(String userId, boolean deleteBiometrics) {
        var t = SessionTable.$;
        sqlClient.createDelete(t)
                .where(t.userId().eq(UUID.fromString(userId)))
                .execute();

        if (!deleteBiometrics)
            return;

        var bt = BiometricTable.$;
        sqlClient.createDelete(bt)
                .where(bt.userId().eq(UUID.fromString(userId)))
                .execute();
    }

    public void deleteOtherSessions(String userId, String sessionId, boolean deleteBiometrics) {
        String deviceId = null;
        if (deleteBiometrics)
            deviceId = getDeviceId(sessionId);

        var t = SessionTable.$;
        sqlClient.createDelete(t)
                .where(Predicate.and(
                        t.userId().eq(UUID.fromString(userId)),
                        t.sessionId().ne(sessionId)
                ))
                .execute();

        if (deviceId == null)
            return;

        var bt = BiometricTable.$;
        sqlClient.createDelete(bt)
                .where(Predicate.and(
                        bt.userId().eq(UUID.fromString(userId)),
                        bt.deviceId().ne(deviceId)
                ))
                .execute();
    }

    public String getUserPassword(String userId) {
        var t = UserTable.$;
        return sqlClient.createQuery(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .select(t.password())
                .execute()
                .getFirst();
    }

    public void updatePassword(String userId, String password) {
        var t = UserTable.$;
        sqlClient.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.password(), password)
                .execute();
    }

    public void updateEmail(String userId, String email, boolean verified) {
        var t = UserTable.$;
        sqlClient.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.email(), email)
                .set(t.emailVerified(), verified)
                .execute();
    }

    public void updatePhone(String userId, String phone, boolean verified) {
        var t = UserTable.$;
        sqlClient.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.phone(), phone)
                .set(t.phoneVerified(), verified)
                .execute();
    }

    public void verifyUserEmail(String userId) {
        var t = UserTable.$;
        sqlClient.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.emailVerified(), true)
                .execute();
    }

    public void verifyUserPhone(String userId) {
        var t = UserTable.$;
        sqlClient.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.phoneVerified(), true)
                .execute();
    }

    /**
     * Retrieves the email and email verification status of a user.
     *
     * @param userId the ID of the user
     * @return a Pair containing the user's email and verification status.
     * The first element is a String representing the email address,
     * and the second element is a boolean indicating whether the
     * email has been verified.
     */
    public Pair<String, Boolean> getUserEmail(String userId) {
        var t = UserTable.$;
        var d = sqlClient.createQuery(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .select(t.email(), t.emailVerified())
                .execute()
                .getFirst();
        return new Pair<>(d.get_1(), d.get_2());
    }

    /**
     * Retrieves the phone and phone verification status of a user
     *
     * @param userId
     * @return a Pair containing the user's phone and verification status.
     * The first element is a String representing the phone number,
     * and the second element is a boolean indicating whether the
     * phone has been verified.
     */
    public Pair<String, Boolean> getUserPhone(String userId) {
        var t = UserTable.$;
        var d = sqlClient.createQuery(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .select(t.phone(), t.phoneVerified())
                .execute()
                .getFirst();
        return new Pair<>(d.get_1(), d.get_2());
    }

    public void enableMfa(String userId, MultiAuthType authType) {
        var t = UserTable.$;
        sqlClient.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.multiFactorAuth(), authType)
                .execute();
    }

    public void disableMfa(String userId) {
        var t = UserTable.$;
        sqlClient.createUpdate(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .set(t.multiFactorAuth(), MultiAuthType.NONE)
                .execute();
    }

    public void createOrUpdateBiometrics(String deviceId, String userId, byte[] publicKey) {
        var b = Biometrics.BIOMETRICS;
        dsl.insertInto(b)
                .set(b.DEVICE_ID, deviceId)
                .set(b.USER_ID, UUID.fromString(userId))
                .set(b.PUBLIC_KEY, publicKey)
                .onConflict(b.DEVICE_ID)
                .doUpdate()
                .set(b.USER_ID, DSL.excluded(b.USER_ID))
                .set(b.PUBLIC_KEY, DSL.excluded(b.PUBLIC_KEY))
                .execute();
    }

    public Optional<BiometricDto> getActiveBiometric(String deviceId) {
        var t = BiometricTable.$;
        return sqlClient.createQuery(t)
                .where(Predicate.and(
                        t.deviceId().eq(deviceId),
                        t.lastUsedAt().gt(OffsetDateTime.now().minusDays(Constants.BIOMETRIC_MAX_STALE_DAYS))))
                .select(t.fetch(BiometricFetcher.$.allScalarFields()))
                .execute()
                .stream()
                .map(biometricMapper::toDto)
                .findFirst();
    }

    public void updateLastUsedBiometric(String deviceId) {
        var t = BiometricTable.$;
        sqlClient.createUpdate(t)
                .where(t.deviceId().eq(deviceId))
                .set(t.lastUsedAt(), OffsetDateTime.now())
                .execute();
    }

    public void removeBiometric(String deviceId) {
        var t = BiometricTable.$;
        sqlClient.createDelete(t)
                .where(t.deviceId().eq(deviceId))
                .execute();
    }
}
