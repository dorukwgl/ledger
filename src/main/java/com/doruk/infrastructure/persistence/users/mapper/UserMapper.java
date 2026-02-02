package com.doruk.infrastructure.persistence.users.mapper;

import com.doruk.application.app.users.dto.CurrentUserDto;
import com.doruk.application.app.users.dto.ProfileDto;
import com.doruk.application.app.users.dto.UserResponseDto;
import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.domain.shared.enums.UserAccountStatus;
import com.doruk.infrastructure.persistence.entity.Role;
import com.doruk.infrastructure.persistence.entity.User;
import jakarta.inject.Singleton;
import org.jooq.Record12;

import java.time.OffsetDateTime;
import java.util.List;


@Singleton
public class UserMapper {
    public CurrentUserDto toCurrentUserDto(Record12<String, String, String, String, Boolean,
            Boolean, MultiAuthType, UserAccountStatus, OffsetDateTime,
            OffsetDateTime, ProfileDto, List<String>> usr) {

        return CurrentUserDto.builder()
                .id(usr.value1())
                .username(usr.value2())
                .email(usr.value3())
                .phone(usr.value4())
                .emailVerified(usr.value5())
                .phoneVerified(usr.value6())
                .multiFactorAuth(usr.value7())
                .status(usr.value8())
                .createdAt(usr.value9())
                .updatedAt(usr.value10())
                .profile(usr.value11())
                .roles(usr.value12())
                .build();
    }
}
