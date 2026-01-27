package com.doruk.infrastructure.exceptionhandlers;

import com.doruk.domain.exception.DomainException;
import com.doruk.infrastructure.dto.ErrorResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = DomainException.class)
public class DomainExceptionHandler implements ExceptionHandler<DomainException, HttpResponse<?>> {
    @Override
    public HttpResponse<?> handle(HttpRequest request, DomainException exception) {
        return HttpResponse.status(400, "Haha, Ha ha")
                .body(new ErrorResponse(exception.getMessage()));
    }
}
