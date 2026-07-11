package com.ucc.oegs.model;

/**
 * A member of teaching staff who authors examinations and reviews results.
 *
 * <p>Another {@link User} subclass, adding the department the instructor
 * belongs to.</p>
 */
public class Instructor extends User {

    private static final long serialVersionUID = 1L;

    private String department;

    public Instructor(String id, String username, String passwordHash,
                      String fullName, String email, String department) {
        super(id, username, passwordHash, fullName, email);
        this.department = department;
    }

    @Override
    public Role getRole() {
        return Role.INSTRUCTOR;
    }

    @Override
    public String getDashboardTitle() {
        return "Instructor Dashboard";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
