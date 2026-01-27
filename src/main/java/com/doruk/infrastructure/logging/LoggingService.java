package com.doruk.infrastructure.logging;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;

@Singleton
public class LoggingService {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingService.class);

    private LoggingService() {
    }

    public static void logError(String message, Throwable ex, Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        sb.append(message).append(" | time=").append(Instant.now());

        if (context != null && !context.isEmpty()) {
            sb.append(" | context=").append(context);
        }

        LOG.error(sb.toString(), ex);
    }

    public static void logError(String message, Throwable ex) {
        logError(message, ex, null);
    }

    public static void logError(String message) {
        LOG.error("{} | time={}", message, Instant.now());
    }

    public static void logWarn(String message) {
        LOG.warn("{} | time={}", message, Instant.now());
    }

    public static void logInfo(String message) {
        LOG.info("{} | time={}", message, Instant.now());
    }
}
