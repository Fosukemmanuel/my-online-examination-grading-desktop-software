package com.ucc.oegs.model;

import java.io.Serializable;

/**
 * Immutable value object holding the outcome of grading one exam:
 * the raw score, percentage, and the UCC letter grade / grade point.
 *
 * <p>Produced by the grading service and stored on a {@link Submission}.</p>
 */
public class GradeResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double scoreObtained;
    private final double totalMarks;
    private final double percentage;
    private final String letterGrade;
    private final double gradePoint;
    private final String interpretation;

    public GradeResult(double scoreObtained, double totalMarks, double percentage,
                       String letterGrade, double gradePoint, String interpretation) {
        this.scoreObtained = scoreObtained;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.letterGrade = letterGrade;
        this.gradePoint = gradePoint;
        this.interpretation = interpretation;
    }

    public double getScoreObtained() {
        return scoreObtained;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public String getInterpretation() {
        return interpretation;
    }

    @Override
    public String toString() {
        return String.format("%.1f/%.1f (%.1f%%) — %s [%s]",
                scoreObtained, totalMarks, percentage, letterGrade, interpretation);
    }
}
