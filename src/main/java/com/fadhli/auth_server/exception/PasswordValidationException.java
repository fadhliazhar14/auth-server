package com.fadhli.auth_server.exception;

import java.util.List;

public class PasswordValidationException extends RuntimeException {
    private final List<String> errors;

    public PasswordValidationException(List<String> errors) {
        super("Password validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
