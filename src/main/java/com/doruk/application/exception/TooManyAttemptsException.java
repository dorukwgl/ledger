package com.doruk.application.exception;

public class TooManyAttemptsException extends ApplicationException {
    public TooManyAttemptsException(String message) {
        super(message);
    }
}
