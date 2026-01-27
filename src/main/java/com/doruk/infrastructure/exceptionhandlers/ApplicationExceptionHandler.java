package com.doruk.infrastructure.exceptionhandlers;

import com.doruk.application.exception.*;
import com.doruk.infrastructure.dto.ErrorResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import javafx.util.Pair;

import java.util.Random;

@Produces
@Singleton
@Requires(classes = ApplicationException.class)
public class ApplicationExceptionHandler implements ExceptionHandler<ApplicationException, HttpResponse<?>> {
    private static final String[] suspiciousMessages = {
            "You knocked from the wrong side of the veil.",
            "This request carries a familiar stench.",
            "Behavior inconsistent with anything human.",
            "Too many masks. None convincing.",
            "We’ve seen this trick before.",
            "You came disguised as traffic. You left as evidence.",
            "Every request leaves a shadow. Yours was loud.",
            "This path closes when intent turns hostile.",
            "Nice user-agent. Very original.",
            "Pretending to be Chrome again? Bold.",
            "If this were subtle, we’d be concerned.",
            "You tried to look normal. That was the mistake."
    };
    private static byte counter = (byte) (new Random().nextInt(0, suspiciousMessages.length));

    private Pair<Integer, String> getStatusCode(ApplicationException exception) {
        return switch (exception) {
            case InvalidCredentialException _ -> new Pair<>(401, "Highly Unauthorized");
            case ForbiddenException _ -> new Pair<>(403, "Interdicted"); // interdicted
            case SuspiciousIntrusionException _ -> new Pair<>(666, "Intrusive Anomaly Detected");
            case ConflictingArgumentException _ -> new Pair<>(409, "Conflict");
            case TooManyAttemptsException _ -> new Pair<>(429, "Too Many Attempts");
            case IncompleteStateException _ -> new Pair<>(422, "Partial Content");
            case InvalidInputException _ -> new Pair<>(400, "Bad Request");
            case RateLimitException _ -> new Pair<>(429, "Cooling Down");
            default -> throw exception; // let the global exception handler handle the server error
        };
    }

    private String getSuspiciousMessage() {
        if (counter >= suspiciousMessages.length)
            counter = 0;

        return suspiciousMessages[counter++];
    }

    private String getMessage(ApplicationException e) {
        var msg = e.getMessage();
        return switch (e) {
            case ForbiddenException _ -> msg == null || msg.isBlank() ?
                    "You are not supposed to perform this action." : msg;
            case RateLimitException _ -> "Slowdown, take a break! Try again after a while.";
            case SuspiciousIntrusionException _ -> getSuspiciousMessage();
            default -> msg;
        };
    }

    @Override
    public HttpResponse<?> handle(HttpRequest request, ApplicationException exception) {
        var statusPair = getStatusCode(exception);
        var msg = getMessage(exception);
        return HttpResponse.status(statusPair.getKey(), statusPair.getValue())
                .body(new ErrorResponse(msg));
    }
}
