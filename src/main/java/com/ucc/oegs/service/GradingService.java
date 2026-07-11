package com.ucc.oegs.service;

import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.GradeResult;
import com.ucc.oegs.model.Question;
import com.ucc.oegs.model.Submission;

/**
 * Marks a {@link Submission} against its {@link Exam} and maps the resulting
 * percentage onto the University of Cape Coast letter-grade scale.
 *
 * <p>The per-question marking is delegated polymorphically to each
 * {@link Question} via {@link Question#grade(String)}, so this service stays
 * agnostic to the mix of question types on the paper.</p>
 *
 * <p><b>UCC grading scale used:</b></p>
 * <pre>
 *   80 – 100 : A  (4.0)  Excellent
 *   75 –  79 : B+ (3.5)  Very Good
 *   70 –  74 : B  (3.0)  Good
 *   65 –  69 : C+ (2.5)  Fairly Good
 *   60 –  64 : C  (2.0)  Average
 *   55 –  59 : D+ (1.5)  Below Average
 *   50 –  54 : D  (1.0)  Pass
 *   45 –  49 : E  (0.5)  Marginal Fail
 *    0 –  44 : F  (0.0)  Fail
 * </pre>
 */
public class GradingService {

    /**
     * Grades every answer on the submission, tallies the score and attaches a
     * {@link GradeResult}. The result is also returned for immediate display.
     */
    public GradeResult grade(Exam exam, Submission submission) {
        double totalMarks = exam.getTotalMarks();
        double score = 0.0;

        for (Question question : exam.getQuestions()) {
            String answer = submission.getAnswerFor(question.getId());
            score += question.grade(answer); // polymorphic dispatch per type
        }

        double percentage = totalMarks > 0 ? (score / totalMarks) * 100.0 : 0.0;
        GradeResult result = classify(score, totalMarks, percentage);
        submission.setResult(result);
        return result;
    }

    /**
     * Converts a percentage into the UCC letter grade, grade point and
     * interpretation. Exposed on its own so it can be unit-tested directly.
     */
    public GradeResult classify(double score, double totalMarks, double percentage) {
        String letter;
        double point;
        String interpretation;

        if (percentage >= 80) {
            letter = "A";  point = 4.0; interpretation = "Excellent";
        } else if (percentage >= 75) {
            letter = "B+"; point = 3.5; interpretation = "Very Good";
        } else if (percentage >= 70) {
            letter = "B";  point = 3.0; interpretation = "Good";
        } else if (percentage >= 65) {
            letter = "C+"; point = 2.5; interpretation = "Fairly Good";
        } else if (percentage >= 60) {
            letter = "C";  point = 2.0; interpretation = "Average";
        } else if (percentage >= 55) {
            letter = "D+"; point = 1.5; interpretation = "Below Average";
        } else if (percentage >= 50) {
            letter = "D";  point = 1.0; interpretation = "Pass";
        } else if (percentage >= 45) {
            letter = "E";  point = 0.5; interpretation = "Marginal Fail";
        } else {
            letter = "F";  point = 0.0; interpretation = "Fail";
        }

        return new GradeResult(score, totalMarks, percentage, letter, point, interpretation);
    }
}
