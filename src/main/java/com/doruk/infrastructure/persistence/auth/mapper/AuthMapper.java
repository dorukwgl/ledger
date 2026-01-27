package com.doruk.infrastructure.persistence.auth.mapper;

import com.doruk.application.app.auth.dto.AuthDto;
import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.persistence.entity.Permission;
import jakarta.inject.Singleton;
import org.babyfish.jimmer.sql.ast.tuple.Tuple9;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
public class AuthMapper {
    public AuthDto toAuthDto(List<Tuple9<UUID, String, String, String, String, Boolean, Boolean, MultiAuthType, Permission>> dt) {
        var commons = dt.getFirst();
        Set<Permissions> permissions = new HashSet<>();
        if (commons.get_9() != null) // null check if permissions is empty
            dt.forEach(tup ->
                    permissions.add(Permissions.valueOf(tup.get_9().name())));

        return AuthDto.builder()
                .id(commons.get_1().toString())
                .username(commons.get_2())
                .password(commons.get_3())
                .phone(commons.get_4())
                .email(commons.get_5())
                .emailVerified(commons.get_6())
                .phoneVerified(commons.get_7())
                .multiFactorAuth(commons.get_8())
                .permissions(permissions)
                .build();
    }
}
