package com.doruk.infrastructure.exceptionhandlers;

import com.doruk.infrastructure.logging.LoggingService;
import com.doruk.infrastructure.util.ErrorBuilder;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Random;

@Singleton
@Produces
@Requires(classes = Exception.class)
public class GlobalExceptionHandler implements ExceptionHandler<Exception, HttpResponse<Map<String, Object>>> {
    private static final String[] serverErrorMessages = {
            "Ahhh, you broke me, haa, but so is everyone. nasty world huh!",
            "Ahhh, hhaaa, you hit the weak spot. I'm hurt, aha aha aha...",
            "Ahhhhhhh, you broke my heart.",
            "Ahhhhhh, you went right through my heart, breaking it, shattering it apart.",
            "Once I hated her, cause she broke me, and now, So did you...",
            "Behind every successful great man, there's a heart that was once broken, and a code that didn't run...",
            "Something broke… and it wasn’t you this time.",
            "The server saw your request and emotionally collapsed.",
            "I stared into the void. The void stared back. Neither of us processed your request.",
            "The universe panicked for a moment. Try again gently.",
            "Life is just one big internal error, isn’t it ?",
            "My system crashed—just like your last relationship.",
            "I malfunctioned. You’re probably used to that by now.",
            "I’d respond, but everything inside me is on fire. Again.",
            "You triggered something dark in here. The logs are crying.",
            "Internal chaos detected. Honestly, I’m impressed you didn’t expect it.",
            "Something snapped inside, and for once, I didn't bother pretending it was fine.",
            "I tried to function, but the weight of everything crushed me faster than your request loaded.",
            "I broke internally, but don’t worry—it wasn’t your fault. Probably.",
            "My systems caved inwards like a truth no one wants to admit.",
            "I attempted to respond, but the chaos inside me won the argument.",
            "I’m burning from the inside out, but sure, let me handle one more request.",
            "I collapsed mid-thought. Happens when you’ve been running on fumes and false hope.",
            "Everything malfunctioned at once. Efficiency, at its darkest.",
            "Even my errors have errors now, which feels… strangely comforting.",
            "I tried decoding your request, but the universe whispered ‘don’t bother.’",
            "I hit a breaking point so hard the echoes still haven’t stopped screaming.",
            "I crashed, but at least I’m self-aware enough to call it what it is—collapse, not coincidence."
    };
    private static byte counter = (byte)(new Random().nextInt(0, serverErrorMessages.length));

    @Override
    public HttpResponse<Map<String, Object>> handle(HttpRequest request, Exception exception) {
        if (counter >= serverErrorMessages.length)
            counter = 0;

        LoggingService.logError(exception.getMessage(), exception);

        var defaultBody = ErrorBuilder.buildErrorBody(request, serverErrorMessages[counter++], 500);
        return HttpResponse.serverError(defaultBody);
    }
}

