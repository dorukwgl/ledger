package com.doruk.infrastructure.persistence.users.mapper;

import com.doruk.application.app.users.dto.ProfileDto;
import com.doruk.infrastructure.persistence.entity.UserProfile;
import io.micronaut.context.annotation.Mapper;

public interface ProfileMapper {
    @Mapper
    ProfileDto toProfileDto(UserProfile userProfile);
}
