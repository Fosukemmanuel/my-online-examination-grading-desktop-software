package com.ucc.oegs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An examination authored by an {@link Instructor}: a titled, timed collection
 * of {@link Question}s that students can attempt while it is published.
 *
 * <p>Encapsulates its questions behind defensive copies and exposes derived
 * values (total marks, question count) rather than letting callers recompute
 * them.</p>
 */
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String title;
    private String courseCode;
    private String description;
    private int durationMinutes;
    private boolean published;
    private final String createdByInstructorId;
    private final LocalDateTime createdAt;
    private final List<Question> questions;

    public Exam(String id, String title, String courseCode, String description,
                int durationMinutes, String createdByInstructorId) {
        this.id = id;
        this.title = title;
        this.courseCode = courseCode;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.createdByInstructorId = createdByInstructorId;
        this.published = false;
        this.createdAt = LocalDateTime.now();
        this.questions = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }

    /** @return an unmodifiable-style copy so callers cannot mutate the internal list. */
    public List<Question> getQuestions() {
        return new ArrayList<>(questions);
    }

    public int getQuestionCount() {
        return questions.size();
    }

    /** @return the sum of the mark value of every question on the paper. */
    public double getTotalMarks() {
        double total = 0.0;
        for (Question q : questions) {
            total += q.getMarks();
        }
        return total;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getCreatedByInstructorId() {
        return createdByInstructorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return courseCode + " — " + title;
    }
}
