package com.ucc.oegs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * A student's completed attempt at an {@link Exam}: the answers they gave and
 * the {@link GradeResult} produced when those answers were marked.
 *
 * <p>Answers are keyed by question id so the grader can look each one up
 * independently of question order.</p>
 */
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String examId;
    private final String studentId;
    private final Map<String, String> answers;
    private final LocalDateTime submittedAt;
    private GradeResult result;

    public Submission(String id, String examId, String studentId,
                      Map<String, String> answers) {
        this.id = id;
        this.examId = examId;
        this.studentId = studentId;
        this.answers = new HashMap<>(answers);
        this.submittedAt = LocalDateTime.now();
    }

    public String getAnswerFor(String questionId) {
        return answers.get(questionId);
    }

    public Map<String, String> getAnswers() {
        return new HashMap<>(answers);
    }

    public String getId() {
        return id;
    }

    public String getExamId() {
        return examId;
    }

    public String getStudentId() {
        return studentId;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public GradeResult getResult() {
        return result;
    }

    public void setResult(GradeResult result) {
        this.result = result;
    }
}
