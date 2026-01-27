package com.doruk.application.exception;

import jakarta.inject.Singleton;

@Singleton
public class ApplicationException extends RuntimeException {
    ApplicationException() {
        super();
    }
    ApplicationException(String message) {
        super(message);
    }

    ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
