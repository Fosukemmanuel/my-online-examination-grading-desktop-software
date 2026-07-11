package com.ucc.oegs.service;

import com.ucc.oegs.exception.ValidationException;
import com.ucc.oegs.model.Administrator;
import com.ucc.oegs.model.Instructor;
import com.ucc.oegs.model.Role;
import com.ucc.oegs.model.Student;
import com.ucc.oegs.model.User;
import com.ucc.oegs.persistence.DataStore;
import com.ucc.oegs.util.IdGenerator;
import com.ucc.oegs.util.PasswordUtil;
import com.ucc.oegs.util.Validator;

import java.util.List;

/**
 * Creates and manages {@link User} accounts and seeds the default logins the
 * first time the application runs.
 */
public class UserService {

    private final DataStore dataStore;

    public UserService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /** Registers a new account of the given role after validating all fields. */
    public User register(Role role, String fullName, String username, String password,
                         String email, String extra1, String extra2)
            throws ValidationException {

        Validator.requireNonBlank(fullName, "Full name");
        Validator.requireNonBlank(username, "Username");
        Validator.requireMinLength(password, 4, "Password");
        Validator.requireEmail(email);

        if (findByUsername(username) != null) {
            throw new ValidationException("That username is already taken.");
        }

        String id = IdGenerator.userId();
        String hash = PasswordUtil.hash(password);
        User user = switch (role) {
            case STUDENT -> {
                Validator.requireNonBlank(extra1, "Index number");
                yield new Student(id, username.trim(), hash, fullName.trim(), email.trim(),
                        extra1.trim(), extra2 == null ? "" : extra2.trim());
            }
            case INSTRUCTOR -> new Instructor(id, username.trim(), hash, fullName.trim(),
                    email.trim(), extra1 == null ? "" : extra1.trim());
            case ADMINISTRATOR -> new Administrator(id, username.trim(), hash,
                    fullName.trim(), email.trim());
        };

        dataStore.addUser(user);
        return user;
    }

    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (User user : dataStore.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return user;
            }
        }
        return null;
    }

    public User findById(String id) {
        for (User user : dataStore.getUsers()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return dataStore.getUsers();
    }

    public boolean deleteUser(User user) {
        return dataStore.removeUser(user);
    }

    /**
     * Populates a fresh install with one account per role so the app can be
     * demonstrated immediately. Runs only when no users exist yet.
     */
    public void seedDefaultsIfEmpty() {
        if (!dataStore.getUsers().isEmpty()) {
            return;
        }
        try {
            register(Role.ADMINISTRATOR, "System Administrator", "admin", "admin123",
                    "admin@ucc.edu.gh", null, null);
            register(Role.INSTRUCTOR, "Dr. Ama Mensah", "instructor", "teach123",
                    "a.mensah@ucc.edu.gh", "Science and Mathematics", null);
            register(Role.STUDENT, "Kwame Boateng", "student", "study123",
                    "k.boateng@stu.ucc.edu.gh", "PG/IT/2025/001", "MSc Information Technology");
        } catch (ValidationException ex) {
            // The seed data is hard-coded and valid; a failure here is a programming error.
            throw new IllegalStateException("Failed to seed default users", ex);
        }
    }
}
