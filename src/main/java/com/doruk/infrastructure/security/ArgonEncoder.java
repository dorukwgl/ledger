package com.doruk.infrastructure.security;

import com.doruk.application.security.PasswordEncoder;
import com.doruk.infrastructure.config.AppExecutors;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;
import io.micronaut.context.annotation.Bean;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Singleton
@Bean(typed = PasswordEncoder.class)
public class ArgonEncoder implements PasswordEncoder {
    private final Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
    private final AppExecutors appExecutors;

    public ArgonEncoder(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public String encode(String password) {
        // iterations, memory: 64MB, parallelism: 2
        var future = CompletableFuture.supplyAsync(() -> argon2.hash(11, 65536, 2, password.toCharArray()),
                appExecutors.CPU());

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean matches(String password, String encodedPassword) {
        var res = CompletableFuture.supplyAsync(() -> argon2.verify(encodedPassword, password.toCharArray()),
                        appExecutors.CPU());
        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
