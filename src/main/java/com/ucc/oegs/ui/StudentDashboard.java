package com.ucc.oegs.ui;

import com.ucc.oegs.OegsApp;
import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.GradeResult;
import com.ucc.oegs.model.Submission;
import com.ucc.oegs.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Student home screen with two tabs: examinations available to sit, and a
 * history of past results with their UCC grades.
 */
public class StudentDashboard {

    private static final DateTimeFormatter WHEN =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final OegsApp app;
    private final ListView<Exam> availableList = new ListView<>();
    private final ListView<Submission> resultsList = new ListView<>();

    public StudentDashboard(OegsApp app) {
        this.app = app;
    }

    public Region build() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab available = new Tab("Available Exams", buildAvailableTab());
        Tab results = new Tab("My Results", buildResultsTab());
        tabs.getTabs().addAll(available, results);

        reloadAvailable();
        reloadResults();

        VBox content = new VBox(12,
                UiHelper.heading("Welcome, " + me().getFullName()),
                tabs);
        VBox.setVgrow(tabs, Priority.ALWAYS);
        return Chrome.page(app, "Student Dashboard", content);
    }

    private User me() {
        return app.auth().getCurrentUser();
    }

    // ----- Available exams tab ----------------------------------------------

    private Region buildAvailableTab() {
        availableList.setCellFactory(v -> new AvailableCell());
        VBox.setVgrow(availableList, Priority.ALWAYS);

        Button start = new Button("Start selected exam");
        start.getStyleClass().add("primary-button");
        start.setOnAction(e -> startSelected());

        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("ghost-button");
        refresh.setOnAction(e -> reloadAvailable());

        VBox box = new VBox(12, availableList, new HBox(10, start, refresh));
        box.setPadding(new Insets(16));
        return box;
    }

    private void reloadAvailable() {
        List<Exam> exams = app.exams().getAvailableExamsFor(me().getId());
        availableList.setItems(FXCollections.observableArrayList(exams));
    }

    private void startSelected() {
        Exam exam = availableList.getSelectionModel().getSelectedItem();
        if (exam == null) {
            UiHelper.showError("No selection", "Please select an exam to start.");
            return;
        }
        boolean ready = UiHelper.confirm("Start \"" + exam.getTitle() + "\"?",
                "You will have " + exam.getDurationMinutes()
                        + " minute(s) and cannot retake it once submitted. Begin now?");
        if (ready) {
            new ExamTakingView(app, exam, () -> {
                reloadAvailable();
                reloadResults();
            }).open();
        }
    }

    // ----- Results tab -------------------------------------------------------

    private Region buildResultsTab() {
        resultsList.setCellFactory(v -> new ResultCell());
        VBox.setVgrow(resultsList, Priority.ALWAYS);

        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("ghost-button");
        refresh.setOnAction(e -> reloadResults());

        VBox box = new VBox(12, resultsList, new HBox(10, refresh));
        box.setPadding(new Insets(16));
        return box;
    }

    private void reloadResults() {
        List<Submission> subs = app.exams().getSubmissionsForStudent(me().getId());
        resultsList.setItems(FXCollections.observableArrayList(subs));
    }

    // ----- Cell renderers ----------------------------------------------------

    private class AvailableCell extends ListCell<Exam> {
        @Override
        protected void updateItem(Exam exam, boolean empty) {
            super.updateItem(exam, empty);
            if (empty || exam == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            Label title = new Label(exam.getCourseCode() + " — " + exam.getTitle());
            title.getStyleClass().add("list-title");
            Label meta = new Label(String.format("%d question(s) · %.0f marks · %d min",
                    exam.getQuestionCount(), exam.getTotalMarks(), exam.getDurationMinutes()));
            meta.getStyleClass().add("list-meta");
            VBox box = new VBox(2, title, meta);
            box.setPadding(new Insets(6, 4, 6, 4));
            setGraphic(box);
            setText(null);
        }
    }

    private class ResultCell extends ListCell<Submission> {
        @Override
        protected void updateItem(Submission sub, boolean empty) {
            super.updateItem(sub, empty);
            if (empty || sub == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            Exam exam = app.exams().findExamById(sub.getExamId());
            String examTitle = exam == null ? sub.getExamId()
                    : exam.getCourseCode() + " — " + exam.getTitle();
            GradeResult r = sub.getResult();

            Label title = new Label(examTitle);
            title.getStyleClass().add("list-title");
            Label meta = new Label(String.format("%.1f / %.1f  ·  %.1f%%  ·  taken %s",
                    r.getScoreObtained(), r.getTotalMarks(), r.getPercentage(),
                    sub.getSubmittedAt().format(WHEN)));
            meta.getStyleClass().add("list-meta");

            Label grade = new Label(r.getLetterGrade());
            grade.getStyleClass().add(r.getGradePoint() >= 1.0 ? "grade-pass" : "grade-fail");
            Label interp = new Label(r.getInterpretation());
            interp.getStyleClass().add("list-meta");
            VBox gradeBox = new VBox(2, grade, interp);
            gradeBox.setAlignment(Pos.CENTER_RIGHT);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox row = new HBox(12, new VBox(2, title, meta), spacer, gradeBox);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 4, 6, 4));
            setGraphic(row);
            setText(null);
        }
    }
}
