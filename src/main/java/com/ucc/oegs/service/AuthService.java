package com.ucc.oegs.service;

import com.ucc.oegs.exception.AuthenticationException;
import com.ucc.oegs.model.User;
import com.ucc.oegs.persistence.DataStore;
import com.ucc.oegs.util.PasswordUtil;

/**
 * Handles credential checking and tracks who is currently signed in.
 *
 * <p>Keeps a single "current user" reference that the UI consults to enforce
 * role-based access. Throwing {@link AuthenticationException} (rather than
 * returning {@code null}) forces callers to deal with failed logins explicitly.</p>
 */
public class AuthService {

    private final DataStore dataStore;
    private User currentUser;

    public AuthService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Validates the supplied credentials and, on success, records the user as
     * the active session.
     *
     * @throws AuthenticationException if the fields are blank, the username is
     *         unknown, or the password does not match
     */
    public User login(String username, String password) throws AuthenticationException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Username and password are both required.");
        }

        for (User user : dataStore.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                if (PasswordUtil.matches(password, user.getPasswordHash())) {
                    this.currentUser = user;
                    return user;
                }
                throw new AuthenticationException("Incorrect password. Please try again.");
            }
        }
        throw new AuthenticationException("No account found for username '" + username.trim() + "'.");
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
