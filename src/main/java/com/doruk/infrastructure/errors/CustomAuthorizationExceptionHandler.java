package com.doruk.infrastructure.errors;

import com.doruk.infrastructure.util.ErrorBuilder;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.authentication.DefaultAuthorizationExceptionHandler;
import io.micronaut.security.authentication.WwwAuthenticateChallengeProvider;
import io.micronaut.security.config.RedirectConfiguration;
import io.micronaut.security.config.RedirectService;
import io.micronaut.security.errors.PriorToLoginPersistence;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
@Replaces(DefaultAuthorizationExceptionHandler.class)
public class CustomAuthorizationExceptionHandler extends DefaultAuthorizationExceptionHandler {
    private final ErrorResponseProcessor errorResponseProcessor;

    public CustomAuthorizationExceptionHandler(
            ErrorResponseProcessor<?> errorResponseProcessor,
            RedirectConfiguration redirectConfiguration,
            RedirectService redirectService,
            List<WwwAuthenticateChallengeProvider<HttpRequest<?>>> wwwAuthenticateChallengeProviders,
            @Nullable PriorToLoginPersistence priorToLoginPersistence, CustomErrorResponseProcessor customErrorResponseProcessor) {
        super(errorResponseProcessor, redirectConfiguration, redirectService, wwwAuthenticateChallengeProviders, priorToLoginPersistence);
        this.errorResponseProcessor = errorResponseProcessor;
    }

    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, AuthorizationException exception) {
        if (!exception.isForbidden()) {
            return HttpResponse.unauthorized().body(
                    ErrorBuilder.buildErrorBody(request, "Please Sign In to perform this action.", 401));
        }

        return super.handle(request, exception);
    }
}
