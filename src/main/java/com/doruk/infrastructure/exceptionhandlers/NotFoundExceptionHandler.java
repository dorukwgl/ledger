package com.doruk.infrastructure.exceptionhandlers;

import com.doruk.infrastructure.util.ErrorBuilder;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.NotFoundException;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Random;

@Produces
@Singleton
@Requires(classes = NotFoundException.class)
public class NotFoundExceptionHandler implements ExceptionHandler<NotFoundException, HttpResponse<Map<String, Object>>> {
    private static final String[] notFoundMessages = {
            "you must be really very desperate, to come such fast",
            "Is is really your love, you searching me in every page that doesn't exists...",
            "Hey heartful man ? how does it feel ? " +
                    "Searching for love, Wandering in every page that doesn't exists ?, " +
                    "Must be exhausting, take a rest.",
            "You looked for me… but I’m just an echo in an empty directory.",
            "Your request reached the universe… but the universe shrugged.",
            "Love not found. Maybe check your spelling, or your destiny.",
            "You chased a dream, but it was a broken link. Happens to the best of us.",
            "The page ran away. Honestly, can't blame it.",
            "You wandered too far, traveler… nothing but digital dust out here.",
            "Not all who wander are lost, but you… you definitely took a wrong turn.",
            "The thing you seek? It never lived here. Or maybe it left long ago.",
            "Somewhere out there exists what you seek. But not here, champ.",
            "Not found—just like the people who swore they’d never leave.",
            "Keep looking. Missing things is kind of your specialty, isn’t it ?",
            "A void responds. That’s all you get. Learn to love the emptiness.",
            "What you seek? Gone. Evaporated. Like promises made at 2 a.m.",
            "You expected content ? Wow. Optimistic.",
            "I’d help you, but I’m as lost as your sense of direction.",
            "Search harder. Maybe you’ll find yourself instead.",
            "The page ghosted you. Don’t take it personally… actually, do.",
            "Even the server couldn’t handle your request. Relatable.",
            "You came looking for something real, but all you found was the place where things come to die.",
            "I vanished long before you arrived, but look at you… still hoping I’d be here.",
            "What you seek isn’t missing—it simply chose to exist somewhere you don’t.",
            "You’re searching in ruins and expecting warmth. Bold. Wrong, but bold.",
            "This place is empty, but honestly, so was everything that led you here.",
            "You knocked, but the silence answered quicker than I ever could.",
            "You’re chasing ghosts again, and even they’re tired of running from you.",
            "If you feel abandoned, good—now you understand this page perfectly.",
            "You didn’t lose anything; it simply slipped away the moment it felt you reaching for it.",
            "This void does not love you back, but hey, at least it’s honest.",
            "Nothing lives here anymore. Maybe it never did. Maybe neither did the things you waited for.",
            "You expected meaning in a hollow space. Cute. Painfully cute.",
            "The thing you came for escaped reality. Just like those who promised forever with you."
    };
    private static byte counter = (byte)(new Random().nextInt(0, notFoundMessages.length));

    @Override
    public HttpResponse<Map<String, Object>> handle(HttpRequest request, NotFoundException exception) {
        if (counter >= notFoundMessages.length)
            counter = 0;

        var body = ErrorBuilder.buildErrorBody(request, notFoundMessages[counter++], 404);
        return HttpResponse.notFound(body);
    }
}
