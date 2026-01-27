package com.doruk.infrastructure.persistence.users;

import com.doruk.application.app.users.dto.*;
import com.doruk.application.dto.UploadedFile;
import com.doruk.infrastructure.persistence.entity.*;
import com.doruk.infrastructure.persistence.users.mapper.ProfileMapper;
import com.doruk.infrastructure.persistence.users.mapper.UserMapper;
import jakarta.inject.Singleton;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.fetcher.ReferenceFetchType;

import java.util.*;

@Singleton
@RequiredArgsConstructor
public class UserRepository {
    private final JSqlClient sqlClient;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

//    public Mono<UserDto> findByEmailOrUsername(String field) {
//        var table = UserTable.$;
//        return Mono.fromCallable(() -> sqlClient.createQuery(table)
//                    .where(Predicate.or(
//                            table.username().eq(field),
//                            table.email().eq(field)
//                    ))
//                    .select(table)
//                    .fetchFirst())
//                .subscribeOn(executors.BLOCKING);
//    }

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
        var draft = UserDraft.$.produce(u ->
                u.setUsername(dto.username().toLowerCase(Locale.ROOT))
                        .setEmail(dto.email().toLowerCase(Locale.ROOT))
                        .setPhone(dto.phone())
                        .setPassword(hashedPassword)
                        .setRoles(List.of(RoleDraft.$.produce(r -> r.setName("USER"))))
        );

        var id = sqlClient.transaction(() -> sqlClient.saveCommand(draft).execute()
                .getModifiedEntity().id());
        var user = sqlClient.findById(User.class, id);

        return userMapper.toResponseDto(Objects.requireNonNull(user));
    }

    public Pair<String, String> getMailAddress(String id) {
        var t = UserTable.$;
        var user = sqlClient.createQuery(t)
                .where(t.id().eq(UUID.fromString(id)))
                .select(t.username(), t.email())
                .execute();

        return user.isEmpty() ? null :
                new Pair<>(user.getFirst().get_1(), user.getFirst().get_2());
    }

    public ProfileDto updateProfile(String userId, ProfileDto dto) {
        var draft = UserProfileDraft.$.produce(d -> d
                .setFullName(dto.fullName())
                .setAddress(dto.address())
                .setCity(dto.city())
                .setState(dto.state())
                .setCountry(dto.country())
                .setPostalCode(dto.postalCode())
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
        var t = UserTable.$;
        var userLst = sqlClient.createQuery(t)
                .where(t.id().eq(UUID.fromString(userId)))
                .select(
                        t.fetch(UserFetcher.$.allScalarFields()
                                .profile(ReferenceFetchType.JOIN_ALWAYS, UserProfileFetcher.$.allScalarFields())
                                .roles()
                        ))
                .execute();

        return userMapper.toCurrentUserDto(userLst.getFirst());
    }
}
