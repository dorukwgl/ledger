package com.doruk.infrastructure.persistence.auth.mapper;

import com.doruk.application.app.auth.dto.BiometricDto;
import com.doruk.infrastructure.persistence.entity.Biometric;
import io.micronaut.context.annotation.Mapper;

public interface BiometricMapper {
    @Mapper
    BiometricDto toDto(Biometric biometric);
}
