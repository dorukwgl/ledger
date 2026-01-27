package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.Context;
import io.micronaut.scheduling.TaskExecutors;
import jakarta.inject.Named;

import java.util.concurrent.ExecutorService;

@Context
public record AppExecutors(
        @Named("cpu-executor")
        ExecutorService CPU,
        @Named(TaskExecutors.BLOCKING)
        ExecutorService VIRTUAL
) {
}