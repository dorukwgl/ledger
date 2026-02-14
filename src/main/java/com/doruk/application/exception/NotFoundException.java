package com.doruk.application.exception;

public class NotFoundException extends ApplicationException {
    public NotFoundException() {
        super();
    }
    public NotFoundException(String message) {
        super(message);
    }
}
