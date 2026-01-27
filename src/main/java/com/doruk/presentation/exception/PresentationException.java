package com.doruk.presentation.exception;

public class PresentationException extends RuntimeException {
    PresentationException(String message) {
        super(message);
    }

    PresentationException(String message, Throwable cause) {
        super(message, cause);
    }
}
