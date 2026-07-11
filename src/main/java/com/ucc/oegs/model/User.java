package com.ucc.oegs.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract base type for every account in the system.
 *
 * <p><b>Encapsulation:</b> all state is private and reached only through
 * accessor/mutator methods that enforce simple invariants.</p>
 *
 * <p><b>Inheritance / Abstraction:</b> {@code User} captures the identity and
 * credential fields common to all accounts, while the abstract
 * {@link #getRole()} and {@link #getDashboardTitle()} methods force each
 * subclass to describe its own role and landing screen.</p>
 */
public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private final LocalDateTime createdAt;

    protected User(String id, String username, String passwordHash,
                   String fullName, String email) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Each concrete account type declares the {@link Role} it represents.
     * Callers rely on this polymorphic method for access-control decisions.
     */
    public abstract Role getRole();

    /**
     * Human-friendly heading shown at the top of the role's dashboard.
     */
    public abstract String getDashboardTitle();

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return fullName + " (" + getRole().getDisplayName() + ")";
    }
}
