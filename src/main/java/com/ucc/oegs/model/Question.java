package com.ucc.oegs.model;

/**
 * Abstract base for every question that can appear on an examination.
 *
 * <p><b>Abstraction:</b> a question knows its text and mark value but leaves the
 * definition of "correct" to its subclasses via the abstract
 * {@link #grade(String)} method inherited from {@link Gradable}.</p>
 *
 * <p><b>Polymorphism:</b> the grading engine holds a list of {@code Question}
 * references and calls {@code grade(...)} on each without knowing (or caring)
 * which concrete type it is dealing with.</p>
 */
public abstract class Question implements Gradable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String text;
    private double marks;

    protected Question(String id, String text, double marks) {
        this.id = id;
        this.text = text;
        this.marks = marks;
    }

    /**
     * @return a short label describing the concrete question type,
     *         used by the UI to render the appropriate answer controls
     */
    public abstract String getType();

    /**
     * @return {@code true} when this question can be marked automatically
     *         without human involvement (objective questions)
     */
    public abstract boolean isAutoGradable();

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "[" + getType() + ", " + marks + " marks] " + text;
    }
}
