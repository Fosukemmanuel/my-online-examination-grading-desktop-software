package com.ucc.oegs.model;

/**
 * A system administrator who manages user accounts.
 *
 * <p>The most privileged {@link User} subclass. Kept deliberately small: its
 * elevated capabilities are expressed through the {@link Role#ADMINISTRATOR}
 * role rather than extra state.</p>
 */
public class Administrator extends User {

    private static final long serialVersionUID = 1L;

    public Administrator(String id, String username, String passwordHash,
                         String fullName, String email) {
        super(id, username, passwordHash, fullName, email);
    }

    @Override
    public Role getRole() {
        return Role.ADMINISTRATOR;
    }

    @Override
    public String getDashboardTitle() {
        return "Administrator Dashboard";
    }
}
