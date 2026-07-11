package com.ucc.oegs.exception;

/** Thrown when login credentials are missing, malformed or incorrect. */
public class AuthenticationException extends OegsException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
