package com.doruk.infrastructure.security;

import com.doruk.application.security.UserScope;
import com.doruk.domain.shared.enums.Permissions;
import com.nimbusds.jwt.JWT;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.validator.DefaultJwtAuthenticationFactory;
import io.micronaut.security.token.jwt.validator.JwtAuthenticationFactory;
import jakarta.inject.Singleton;

import java.util.*;

@Singleton
@Replaces(DefaultJwtAuthenticationFactory.class)
public class CustomJwtAuthenticationFactory implements JwtAuthenticationFactory {

    @Override
    public Optional<Authentication> createAuthentication(JWT jwt) {
        try {
            Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();

            Object sub = claims.get("sub");
            if (sub == null)
                return Optional.empty();

            // Convert permissions ONCE
            EnumSet<Permissions> permissions = EnumSet.noneOf(Permissions.class);

            Object scp = claims.get("scp");
            if (scp instanceof Iterable<?> iterable) {
                for (Object o : iterable) {
                    permissions.add(
                            Permissions.fromId(((Number) o).intValue())
                    );
                }
            }

            return Optional.of(
                    Authentication.build(
                            sub.toString(),
                            Map.of(
                                    UserScope.KEY,
                                    permissions
                            )
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
