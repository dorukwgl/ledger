package com.doruk.presentation.users.mapper;

import com.doruk.application.app.users.dto.ProfileDto;
import com.doruk.presentation.users.dto.ProfileUpdateRequest;
import io.micronaut.context.annotation.Mapper;

public interface ProfileMapper {
    @Mapper
    ProfileDto toProfileDto(ProfileUpdateRequest req);
}
