package com.doruk.application.app.system.dto;

import com.doruk.application.dto.StoredObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.UUID;

@Serdeable
@Builder
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(UUID id, String username, String email, List<String> permissions, List<String> roles,
                           StoredObject profileIcon, String profileIconUrl) {
}
