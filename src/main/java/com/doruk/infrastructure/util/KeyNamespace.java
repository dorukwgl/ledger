package com.doruk.infrastructure.util;

public class KeyNamespace {
    public static String cooldownPrefix(String prefix, String tid) {
        return prefix + ":cooldown:" + tid;
    }

    public static String attemptPrefix(String prefix, String tid) {
        return prefix + ":attempt:" + tid;
    }

    public static String magicLinkPrefix(String prefix, String tid) {
        return prefix + ":magic:" + tid;
    }

    public static String extractTid(String prefix, String transactionId) {
        if (!transactionId.startsWith(prefix))
            throw new IllegalArgumentException("Transaction Id doesn't start with the given prefix.");

        return transactionId.substring(prefix.length());
    }

    public static String mfaTransaction() {
        return "mfa:txn:";
    }

    public static String verificationTransaction() {
        return "vrf:txn:";
    }

    public static String updateAuthTransaction() {
        return "auth:update:txn:";
    }

    public static String resetPasswordTransaction() {
        return "reset:pw:txn:";
    }

    public static String getNamespacedId(String prefix, String tid) {
        return prefix + tid;
    }

    public static String biometricTransaction() {
        return "bmt:txn:device:";
    }

    public static String biometricTxnUserLock() {
        return "bmt:txn:lock:user:";
    }

    public static String biometricTxnDeviceLock() {
        return "bmt:txn:lock:device:";
    }
}
