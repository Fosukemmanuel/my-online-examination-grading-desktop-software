package com.ucc.oegs.model;

import java.io.Serializable;

/**
 * Abstraction for any object that can grade a student's raw answer against
 * its own definition of correctness.
 *
 * <p>This interface is the seam that lets the grading engine treat every
 * question uniformly (polymorphism) while each concrete question type supplies
 * its own marking logic.</p>
 */
public interface Gradable extends Serializable {

    /**
     * Marks the supplied answer.
     *
     * @param submittedAnswer the raw answer captured from the student
     * @return the marks awarded, between {@code 0} and {@link #getMarks()}
     */
    double grade(String submittedAnswer);

    /**
     * @return the maximum marks obtainable for this item
     */
    double getMarks();
}
