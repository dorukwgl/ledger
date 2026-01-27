package com.doruk.infrastructure.startup;

import com.doruk.infrastructure.config.AppConfig;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
@RequiredArgsConstructor
@Requires(env = "setup")
public class DirSeeder {
    private final AppConfig config;

    @EventListener
    public void seedDirs(StartupEvent e) throws Exception {
        Files.createDirectories(Path.of(config.localStorageDir()));

        IO.println("Directories Created...");
    }
}
