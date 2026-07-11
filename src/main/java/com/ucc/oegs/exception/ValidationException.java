package com.ucc.oegs.exception;

/** Thrown when user-supplied input fails a validation rule. */
public class ValidationException extends OegsException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
