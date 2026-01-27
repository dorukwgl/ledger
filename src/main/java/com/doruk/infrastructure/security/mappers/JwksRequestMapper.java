package com.doruk.infrastructure.security.mappers;

import com.doruk.domain.shared.enums.Permissions;
import com.doruk.infrastructure.apiclient.dto.JwksRequest;
import com.doruk.application.app.auth.dto.JwtRequest;
import io.micronaut.context.annotation.Mapper.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface JwksRequestMapper {
    @Mapping(
        to = "sub",
        from = "#{jwtRequest.id}"
    )
    @Mapping(
        to = "aud",
        from = "#{jwtRequest.audience}"
    )
    @Mapping(
        to = "scp",
        from = "#{this.toScopes(jwtRequest.permissions)}"
    )
    JwksRequest toJwksRequest(JwtRequest jwtRequest);

    default List<Integer> toScopes(Set<Permissions> permissions) {
        return permissions.stream()
                .map(Permissions::id)
                .collect(Collectors.toList());
    }
}
