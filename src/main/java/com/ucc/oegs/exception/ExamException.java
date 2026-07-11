package com.ucc.oegs.exception;

/** Thrown when an examination cannot be created, published or attempted. */
public class ExamException extends OegsException {

    private static final long serialVersionUID = 1L;

    public ExamException(String message) {
        super(message);
    }
}
