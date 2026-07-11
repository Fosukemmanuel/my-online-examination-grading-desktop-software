package com.ucc.oegs.model;

/**
 * Identifies the kind of account a {@link User} holds and, by extension,
 * which parts of the system that user is authorised to reach.
 *
 * <p>Using an enum (rather than free-form strings) gives us compile-time
 * safety for role checks and makes the role-based access rules explicit.</p>
 */
public enum Role {
    STUDENT("Student"),
    INSTRUCTOR("Instructor"),
    ADMINISTRATOR("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
