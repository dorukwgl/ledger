package com.doruk.application.exception;

public class ForbiddenException extends ApplicationException {
    public ForbiddenException(String message) {
        super(message);
    }
    public ForbiddenException() {
        super();
    }
}
