package com.fadhli.auth_server.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class PasswordValidationException extends RuntimeException {
    private final List<String> errors;

    public PasswordValidationException(List<String> errors) {
        super("Password validation failed");
        this.errors = errors;
    }
}
