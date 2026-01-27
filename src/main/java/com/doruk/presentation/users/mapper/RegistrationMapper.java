package com.doruk.presentation.users.mapper;

import com.doruk.application.app.users.dto.CreateUserCmd;
import com.doruk.presentation.users.dto.RegistrationRequest;
import io.micronaut.context.annotation.Mapper;

public interface RegistrationMapper {
    @Mapper
    CreateUserCmd toUserCmdDto(RegistrationRequest request);
}
