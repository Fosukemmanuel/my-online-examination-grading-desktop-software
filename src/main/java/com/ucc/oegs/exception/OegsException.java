package com.ucc.oegs.exception;

/**
 * Root of the application's checked exception hierarchy.
 *
 * <p>Grouping every domain failure under one parent lets UI code catch
 * {@code OegsException} broadly for a friendly message, while specific handlers
 * can still target {@link AuthenticationException}, {@link ValidationException}
 * or {@link ExamException} individually.</p>
 */
public class OegsException extends Exception {

    private static final long serialVersionUID = 1L;

    public OegsException(String message) {
        super(message);
    }

    public OegsException(String message, Throwable cause) {
        super(message, cause);
    }
}
