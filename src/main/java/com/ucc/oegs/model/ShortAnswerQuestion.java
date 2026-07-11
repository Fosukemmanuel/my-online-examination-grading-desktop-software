package com.ucc.oegs.model;

/**
 * A short free-text question.
 *
 * <p>Marked by comparing the student's response against a set of acceptable
 * keywords/phrases (case-insensitive, whitespace-trimmed). This keeps simple
 * factual answers auto-gradable while still being far more forgiving than an
 * exact-string match.</p>
 */
public class ShortAnswerQuestion extends Question {

    private static final long serialVersionUID = 1L;

    /** Pipe-separated list of accepted answers, e.g. {@code "encapsulation|data hiding"}. */
    private String acceptedAnswers;

    public ShortAnswerQuestion(String id, String text, double marks, String acceptedAnswers) {
        super(id, text, marks);
        this.acceptedAnswers = acceptedAnswers;
    }

    @Override
    public String getType() {
        return "Short Answer";
    }

    @Override
    public boolean isAutoGradable() {
        return true;
    }

    @Override
    public double grade(String submittedAnswer) {
        if (submittedAnswer == null || submittedAnswer.isBlank()
                || acceptedAnswers == null || acceptedAnswers.isBlank()) {
            return 0.0;
        }
        String normalised = submittedAnswer.trim().toLowerCase();
        for (String accepted : acceptedAnswers.split("\\|")) {
            if (normalised.equals(accepted.trim().toLowerCase())) {
                return getMarks();
            }
        }
        return 0.0;
    }

    public String getAcceptedAnswers() {
        return acceptedAnswers;
    }

    public void setAcceptedAnswers(String acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers;
    }
}
