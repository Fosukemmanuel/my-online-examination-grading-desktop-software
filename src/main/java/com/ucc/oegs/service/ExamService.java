package com.ucc.oegs.service;

import com.ucc.oegs.exception.ExamException;
import com.ucc.oegs.exception.ValidationException;
import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.GradeResult;
import com.ucc.oegs.model.Question;
import com.ucc.oegs.model.Submission;
import com.ucc.oegs.persistence.DataStore;
import com.ucc.oegs.util.IdGenerator;
import com.ucc.oegs.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Application logic for authoring, publishing and attempting examinations.
 *
 * <p>Coordinates the {@link DataStore} (persistence) and the
 * {@link GradingService} (marking) so the UI layer deals only with high-level
 * operations.</p>
 */
public class ExamService {

    private final DataStore dataStore;
    private final GradingService gradingService;

    public ExamService(DataStore dataStore, GradingService gradingService) {
        this.dataStore = dataStore;
        this.gradingService = gradingService;
    }

    /** Creates a new (unpublished) exam owned by the given instructor. */
    public Exam createExam(String title, String courseCode, String description,
                           int durationMinutes, String instructorId)
            throws ValidationException {
        Validator.requireNonBlank(title, "Exam title");
        Validator.requireNonBlank(courseCode, "Course code");
        Validator.requirePositive(durationMinutes, "Duration (minutes)");

        Exam exam = new Exam(IdGenerator.examId(), title.trim(), courseCode.trim(),
                description == null ? "" : description.trim(), durationMinutes, instructorId);
        dataStore.addExam(exam);
        return exam;
    }

    public void addQuestion(Exam exam, Question question) {
        exam.addQuestion(question);
        dataStore.updateExams();
    }

    public void removeQuestion(Exam exam, Question question) {
        exam.removeQuestion(question);
        dataStore.updateExams();
    }

    /**
     * Publishes an exam, making it visible to students.
     *
     * @throws ExamException if the paper has no questions yet
     */
    public void publish(Exam exam) throws ExamException {
        if (exam.getQuestionCount() == 0) {
            throw new ExamException("An exam must contain at least one question before it can be published.");
        }
        exam.setPublished(true);
        dataStore.updateExams();
    }

    public void unpublish(Exam exam) {
        exam.setPublished(false);
        dataStore.updateExams();
    }

    public boolean deleteExam(Exam exam) {
        return dataStore.removeExam(exam);
    }

    public List<Exam> getAllExams() {
        return dataStore.getExams();
    }

    public List<Exam> getExamsByInstructor(String instructorId) {
        List<Exam> result = new ArrayList<>();
        for (Exam exam : dataStore.getExams()) {
            if (exam.getCreatedByInstructorId().equals(instructorId)) {
                result.add(exam);
            }
        }
        return result;
    }

    /** Published exams a student has not yet attempted. */
    public List<Exam> getAvailableExamsFor(String studentId) {
        List<Exam> result = new ArrayList<>();
        for (Exam exam : dataStore.getExams()) {
            if (exam.isPublished() && !hasSubmitted(studentId, exam.getId())) {
                result.add(exam);
            }
        }
        return result;
    }

    public Exam findExamById(String examId) {
        for (Exam exam : dataStore.getExams()) {
            if (exam.getId().equals(examId)) {
                return exam;
            }
        }
        return null;
    }

    public boolean hasSubmitted(String studentId, String examId) {
        for (Submission submission : dataStore.getSubmissions()) {
            if (submission.getStudentId().equals(studentId)
                    && submission.getExamId().equals(examId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Records a student's answers, grades them immediately and stores the
     * resulting submission.
     *
     * @throws ExamException if the student has already sat this exam
     */
    public Submission submitExam(Exam exam, String studentId, Map<String, String> answers)
            throws ExamException {
        if (hasSubmitted(studentId, exam.getId())) {
            throw new ExamException("You have already submitted this exam.");
        }
        Submission submission = new Submission(IdGenerator.submissionId(),
                exam.getId(), studentId, answers);
        GradeResult result = gradingService.grade(exam, submission);
        submission.setResult(result);
        dataStore.addSubmission(submission);
        return submission;
    }

    public List<Submission> getSubmissionsForStudent(String studentId) {
        List<Submission> result = new ArrayList<>();
        for (Submission submission : dataStore.getSubmissions()) {
            if (submission.getStudentId().equals(studentId)) {
                result.add(submission);
            }
        }
        return result;
    }

    public List<Submission> getSubmissionsForExam(String examId) {
        List<Submission> result = new ArrayList<>();
        for (Submission submission : dataStore.getSubmissions()) {
            if (submission.getExamId().equals(examId)) {
                result.add(submission);
            }
        }
        return result;
    }
}
