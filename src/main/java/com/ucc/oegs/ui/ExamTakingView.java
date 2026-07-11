package com.ucc.oegs.ui;

import com.ucc.oegs.OegsApp;
import com.ucc.oegs.exception.ExamException;
import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.GradeResult;
import com.ucc.oegs.model.MultipleChoiceQuestion;
import com.ucc.oegs.model.Question;
import com.ucc.oegs.model.ShortAnswerQuestion;
import com.ucc.oegs.model.Submission;
import com.ucc.oegs.model.TrueFalseQuestion;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Full-screen modal that runs a timed examination.
 *
 * <p>Renders each {@link Question} with input controls appropriate to its type,
 * counts down the allowed duration with a {@link Timeline}, and auto-submits
 * when time runs out. On submission the answers are graded instantly and the
 * UCC result is shown before the window closes.</p>
 */
public class ExamTakingView {

    private final OegsApp app;
    private final Exam exam;
    private final Runnable onFinished;
    private final Stage stage = new Stage();

    /** Question id → a supplier that reads the student's current answer as a string. */
    private final Map<String, AnswerReader> readers = new HashMap<>();

    private Timeline timer;
    private int remainingSeconds;
    private Label clockLabel;
    private boolean submitted = false;

    public ExamTakingView(OegsApp app, Exam exam, Runnable onFinished) {
        this.app = app;
        this.exam = exam;
        this.onFinished = onFinished;
        this.remainingSeconds = exam.getDurationMinutes() * 60;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Sitting: " + exam.getTitle());
    }

    public void open() {
        stage.setScene(new Scene(buildScene(), 820, 680));
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            stage.getScene().getStylesheets().add(css.toExternalForm());
        }
        // Closing the window early counts as giving up: submit what we have.
        stage.setOnCloseRequest(e -> {
            if (!submitted) {
                e.consume();
                if (UiHelper.confirm("Submit and exit?",
                        "Closing now will submit your current answers. Continue?")) {
                    submit(false);
                }
            }
        });
        startTimer();
        stage.showAndWait();
    }

    private BorderPane buildScene() {
        // --- header with title and live clock ---
        Label title = new Label(exam.getCourseCode() + " — " + exam.getTitle());
        title.getStyleClass().add("heading");

        clockLabel = new Label(formatClock(remainingSeconds));
        clockLabel.getStyleClass().add("clock");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(16, title, spacer, clockLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 20, 8, 20));

        // --- questions ---
        VBox questionBox = new VBox(18);
        questionBox.setPadding(new Insets(12, 20, 20, 20));

        List<Question> questions = exam.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            questionBox.getChildren().add(renderQuestion(i + 1, questions.get(i)));
        }

        ScrollPane scroll = new ScrollPane(questionBox);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // --- footer ---
        Button submit = new Button("Submit exam");
        submit.getStyleClass().add("primary-button");
        submit.setOnAction(e -> {
            if (UiHelper.confirm("Submit exam", "Submit your answers for grading now?")) {
                submit(false);
            }
        });
        HBox footer = new HBox(submit);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(12, 20, 16, 20));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setTop(header);
        root.setCenter(scroll);
        root.setBottom(footer);
        return root;
    }

    /** Builds the card for one question, wiring an {@link AnswerReader} for it. */
    private Region renderQuestion(int number, Question question) {
        VBox card = new VBox(10);
        card.getStyleClass().add("question-card");
        card.setPadding(new Insets(16));

        Label prompt = new Label(number + ". " + question.getText());
        prompt.getStyleClass().add("question-text");
        prompt.setWrapText(true);

        Label meta = new Label(question.getType() + " · " + question.getMarks() + " mark(s)");
        meta.getStyleClass().add("list-meta");

        card.getChildren().addAll(prompt, meta);
        card.getChildren().add(buildAnswerControl(question));
        return card;
    }

    private Region buildAnswerControl(Question question) {
        if (question instanceof MultipleChoiceQuestion mcq) {
            ToggleGroup group = new ToggleGroup();
            VBox options = new VBox(6);
            List<String> opts = mcq.getOptions();
            for (int i = 0; i < opts.size(); i++) {
                RadioButton rb = new RadioButton(opts.get(i));
                rb.setToggleGroup(group);
                rb.setUserData(String.valueOf(i)); // store option index
                rb.setWrapText(true);
                options.getChildren().add(rb);
            }
            readers.put(question.getId(), () -> {
                var selected = group.getSelectedToggle();
                return selected == null ? "" : (String) selected.getUserData();
            });
            return options;

        } else if (question instanceof TrueFalseQuestion) {
            ToggleGroup group = new ToggleGroup();
            RadioButton true_ = new RadioButton("True");
            RadioButton false_ = new RadioButton("False");
            true_.setToggleGroup(group);
            false_.setToggleGroup(group);
            true_.setUserData("true");
            false_.setUserData("false");
            readers.put(question.getId(), () -> {
                var selected = group.getSelectedToggle();
                return selected == null ? "" : (String) selected.getUserData();
            });
            return new HBox(20, true_, false_);

        } else if (question instanceof ShortAnswerQuestion) {
            TextField field = new TextField();
            field.setPromptText("Type your answer");
            readers.put(question.getId(), field::getText);
            return field;
        }
        return new Label("Unsupported question type.");
    }

    // ----- Timer -------------------------------------------------------------

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void tick() {
        remainingSeconds--;
        clockLabel.setText(formatClock(remainingSeconds));
        if (remainingSeconds <= 60) {
            clockLabel.getStyleClass().remove("clock");
            if (!clockLabel.getStyleClass().contains("clock-warning")) {
                clockLabel.getStyleClass().add("clock-warning");
            }
        }
        if (remainingSeconds <= 0) {
            submit(true); // time is up — auto-submit
        }
    }

    private String formatClock(int totalSeconds) {
        int s = Math.max(0, totalSeconds);
        return String.format("Time left  %02d:%02d", s / 60, s % 60);
    }

    // ----- Submission --------------------------------------------------------

    private void submit(boolean auto) {
        if (submitted) {
            return;
        }
        submitted = true;
        if (timer != null) {
            timer.stop();
        }

        Map<String, String> answers = new HashMap<>();
        for (var entry : readers.entrySet()) {
            String value = entry.getValue().read();
            answers.put(entry.getKey(), value == null ? "" : value);
        }

        try {
            Submission submission = app.exams().submitExam(
                    exam, app.auth().getCurrentUser().getId(), answers);
            GradeResult r = submission.getResult();
            String heading = auto ? "Time up — exam submitted" : "Exam submitted";
            UiHelper.showInfo(heading, String.format(
                    "%s%n%nScore: %.1f / %.1f (%.1f%%)%nGrade: %s (%.1f) — %s",
                    exam.getTitle(), r.getScoreObtained(), r.getTotalMarks(),
                    r.getPercentage(), r.getLetterGrade(), r.getGradePoint(),
                    r.getInterpretation()));
        } catch (ExamException ex) {
            UiHelper.showError("Could not submit", ex.getMessage());
        } finally {
            onFinished.run();
            stage.close();
        }
    }

    /** Reads a single question's current answer as a string. */
    @FunctionalInterface
    private interface AnswerReader {
        String read();
    }
}
