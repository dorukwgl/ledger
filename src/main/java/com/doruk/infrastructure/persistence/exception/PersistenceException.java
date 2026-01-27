package com.doruk.infrastructure.persistence.exception;

public class PersistenceException extends RuntimeException {
    PersistenceException(String message) {
        super(message);
    }

    PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
