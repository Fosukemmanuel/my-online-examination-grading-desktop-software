package com.ucc.oegs.model;

/**
 * A learner who registers for and sits examinations.
 *
 * <p>Extends {@link User} and adds student-specific state (index number and
 * programme). Demonstrates <b>inheritance</b> by reusing the identity fields of
 * the base class and <b>polymorphism</b> by supplying its own {@link Role}.</p>
 */
public class Student extends User {

    private static final long serialVersionUID = 1L;

    private String indexNumber;
    private String programme;

    public Student(String id, String username, String passwordHash,
                   String fullName, String email,
                   String indexNumber, String programme) {
        super(id, username, passwordHash, fullName, email);
        this.indexNumber = indexNumber;
        this.programme = programme;
    }

    @Override
    public Role getRole() {
        return Role.STUDENT;
    }

    @Override
    public String getDashboardTitle() {
        return "Student Dashboard";
    }

    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }
}
