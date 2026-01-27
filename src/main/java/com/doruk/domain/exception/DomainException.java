package com.doruk.domain.exception;

import jakarta.inject.Singleton;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
