package com.ucc.oegs.util;

import com.ucc.oegs.exception.ValidationException;

/**
 * Reusable, self-contained input-validation checks.
 *
 * <p>Each method throws a {@link ValidationException} carrying a message fit to
 * show the user, so callers can validate then act without writing repetitive
 * {@code if} blocks. Centralising the rules keeps validation consistent across
 * every screen.</p>
 */
public final class Validator {

    private Validator() {
    }

    public static void requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static void requireMinLength(String value, int min, String fieldName)
            throws ValidationException {
        if (value == null || value.trim().length() < min) {
            throw new ValidationException(fieldName + " must be at least " + min + " characters.");
        }
    }

    public static void requireEmail(String value) throws ValidationException {
        requireNonBlank(value, "Email");
        // Deliberately simple, readable pattern rather than a full RFC checker.
        if (!value.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
            throw new ValidationException("Please enter a valid email address.");
        }
    }

    public static void requirePositive(int value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero.");
        }
    }

    public static void requirePositive(double value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero.");
        }
    }

    /** Parses an integer, converting the raw {@link NumberFormatException} into a friendly one. */
    public static int parseInt(String value, String fieldName) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException ex) {
            throw new ValidationException(fieldName + " must be a whole number.");
        }
    }

    /** Parses a decimal, converting the raw {@link NumberFormatException} into a friendly one. */
    public static double parseDouble(String value, String fieldName) throws ValidationException {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException | NullPointerException ex) {
            throw new ValidationException(fieldName + " must be a number.");
        }
    }
}
