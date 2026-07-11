package com.ucc.oegs.service;

import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.GradeResult;
import com.ucc.oegs.model.MultipleChoiceQuestion;
import com.ucc.oegs.model.ShortAnswerQuestion;
import com.ucc.oegs.model.Submission;
import com.ucc.oegs.model.TrueFalseQuestion;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the grading engine: per-type marking and the UCC letter-grade
 * boundaries. These are the parts of the system where a silent off-by-one would
 * corrupt a student's result, so they are worth pinning down with tests.
 */
class GradingServiceTest {

    private final GradingService grading = new GradingService();

    @Test
    void classifiesUccGradeBoundaries() {
        assertEquals("A", grading.classify(80, 100, 80).getLetterGrade());
        assertEquals("B+", grading.classify(79, 100, 79).getLetterGrade());
        assertEquals("B", grading.classify(70, 100, 70).getLetterGrade());
        assertEquals("C+", grading.classify(69, 100, 69).getLetterGrade());
        assertEquals("C", grading.classify(60, 100, 60).getLetterGrade());
        assertEquals("D+", grading.classify(55, 100, 55).getLetterGrade());
        assertEquals("D", grading.classify(50, 100, 50).getLetterGrade());
        assertEquals("E", grading.classify(45, 100, 45).getLetterGrade());
        assertEquals("F", grading.classify(44, 100, 44).getLetterGrade());
    }

    @Test
    void gradesMixedPaperPolymorphically() {
        Exam exam = new Exam("EXM-1", "OOP Quiz", "INF811D", "", 20, "USR-1");
        exam.addQuestion(new MultipleChoiceQuestion("Q1", "Pick B", 2,
                List.of("A", "B", "C"), 1));
        exam.addQuestion(new TrueFalseQuestion("Q2", "Java is OOP", 2, true));
        exam.addQuestion(new ShortAnswerQuestion("Q3", "Hiding data is called?", 1,
                "encapsulation|data hiding"));

        Map<String, String> answers = new HashMap<>();
        answers.put("Q1", "1");                 // correct  -> 2
        answers.put("Q2", "false");             // wrong    -> 0
        answers.put("Q3", "Data Hiding");       // correct  -> 1

        Submission submission = new Submission("SUB-1", exam.getId(), "USR-1", answers);
        GradeResult result = grading.grade(exam, submission);

        assertEquals(3.0, result.getScoreObtained(), 0.0001);
        assertEquals(5.0, result.getTotalMarks(), 0.0001);
        assertEquals(60.0, result.getPercentage(), 0.0001);
        assertEquals("C", result.getLetterGrade());
    }
}
