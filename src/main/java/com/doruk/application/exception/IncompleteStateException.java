package com.doruk.application.exception;

/** thrown when the internal state of the application is incomplete,
 * e.g. when the db doesn't contain phone, but user wants phone mfa.
 */
public class IncompleteStateException extends ApplicationException {
    public IncompleteStateException(String message) {
        super(message);
    }
}
