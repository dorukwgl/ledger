package com.doruk.infrastructure.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateRandom {
    public static String generateSessionId() {
        return UUID.randomUUID().toString() + UUID.randomUUID() + UUID.randomUUID();
    }

    public static String generateMfaToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID();
    }

    public static int generateOtp() {
        return ThreadLocalRandom.current().nextInt(100000, 999999);
    }

    public static String generateTransactionId() {
        return generateMfaToken();
    }
}
