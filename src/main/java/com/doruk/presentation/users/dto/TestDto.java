package com.doruk.presentation.users.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;

@Introspected
@Serdeable
@Getter
public class TestDto {
    private String name;

    public TestDto(com.doruk.infrastructure.persistence.entity.Permission permission) {
        this.name = permission.name();
    }
}
