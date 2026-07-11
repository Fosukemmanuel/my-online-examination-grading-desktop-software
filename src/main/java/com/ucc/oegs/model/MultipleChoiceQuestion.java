package com.ucc.oegs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A single-answer multiple-choice question.
 *
 * <p>Stores an ordered list of options and the index of the correct one.
 * Overrides {@link #grade(String)} to award full marks only when the submitted
 * option index matches the key exactly.</p>
 */
public class MultipleChoiceQuestion extends Question {

    private static final long serialVersionUID = 1L;

    private final List<String> options;
    private int correctOptionIndex;

    public MultipleChoiceQuestion(String id, String text, double marks,
                                  List<String> options, int correctOptionIndex) {
        super(id, text, marks);
        this.options = new ArrayList<>(options);
        this.correctOptionIndex = correctOptionIndex;
    }

    @Override
    public String getType() {
        return "Multiple Choice";
    }

    @Override
    public boolean isAutoGradable() {
        return true;
    }

    /**
     * The submitted answer is expected to be the chosen option's index encoded
     * as a string (e.g. {@code "2"}). A blank or non-numeric answer scores zero
     * rather than throwing, so an unanswered question never breaks marking.
     */
    @Override
    public double grade(String submittedAnswer) {
        if (submittedAnswer == null || submittedAnswer.isBlank()) {
            return 0.0;
        }
        try {
            int chosen = Integer.parseInt(submittedAnswer.trim());
            return chosen == correctOptionIndex ? getMarks() : 0.0;
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }
}
