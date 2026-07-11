package com.ucc.oegs.model;

/**
 * A true/false question. A specialised objective question whose answer space
 * is limited to the two boolean literals.
 */
public class TrueFalseQuestion extends Question {

    private static final long serialVersionUID = 1L;

    private boolean correctAnswer;

    public TrueFalseQuestion(String id, String text, double marks, boolean correctAnswer) {
        super(id, text, marks);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String getType() {
        return "True / False";
    }

    @Override
    public boolean isAutoGradable() {
        return true;
    }

    /**
     * Accepts {@code "true"}/{@code "false"} (case-insensitive) and awards full
     * marks on a match with the stored key.
     */
    @Override
    public double grade(String submittedAnswer) {
        if (submittedAnswer == null || submittedAnswer.isBlank()) {
            return 0.0;
        }
        boolean chosen = Boolean.parseBoolean(submittedAnswer.trim());
        return chosen == correctAnswer ? getMarks() : 0.0;
    }

    public boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
