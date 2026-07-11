package com.ucc.oegs.ui;

import com.ucc.oegs.OegsApp;
import com.ucc.oegs.exception.ExamException;
import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.Submission;
import com.ucc.oegs.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Instructor screen: lists the exams the signed-in instructor owns and offers
 * actions to build a new exam, publish/unpublish, view results, or delete.
 */
public class InstructorDashboard {

    private final OegsApp app;
    private final ListView<Exam> examList = new ListView<>();

    public InstructorDashboard(OegsApp app) {
        this.app = app;
    }

    public Region build() {
        VBox content = new VBox(16);
        content.getChildren().add(UiHelper.heading("My Examinations"));
        content.getChildren().add(UiHelper.subheading(
                "Create timed papers, publish them to students and review results"));

        examList.setCellFactory(v -> new ExamCell());
        VBox.setVgrow(examList, Priority.ALWAYS);

        Button create = new Button("+ New exam");
        create.getStyleClass().add("primary-button");
        create.setOnAction(e -> new ExamBuilderView(app, this::reload).open());

        Button publish = new Button("Publish / Unpublish");
        publish.getStyleClass().add("ghost-button");
        publish.setOnAction(e -> togglePublish());

        Button results = new Button("View results");
        results.getStyleClass().add("ghost-button");
        results.setOnAction(e -> viewResults());

        Button edit = new Button("Add questions");
        edit.getStyleClass().add("ghost-button");
        edit.setOnAction(e -> editSelected());

        Button delete = new Button("Delete");
        delete.getStyleClass().add("danger-button");
        delete.setOnAction(e -> deleteSelected());

        HBox actions = new HBox(10, create, edit, publish, results, delete);
        actions.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(examList, actions);
        reload();
        return Chrome.page(app, "Instructor Dashboard", content);
    }

    private User me() {
        return app.auth().getCurrentUser();
    }

    private void reload() {
        List<Exam> mine = app.exams().getExamsByInstructor(me().getId());
        examList.setItems(FXCollections.observableArrayList(mine));
    }

    private Exam requireSelection() {
        Exam selected = examList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelper.showError("No selection", "Please select an exam first.");
        }
        return selected;
    }

    private void editSelected() {
        Exam exam = requireSelection();
        if (exam != null) {
            new ExamBuilderView(app, this::reload).openFor(exam);
        }
    }

    private void togglePublish() {
        Exam exam = requireSelection();
        if (exam == null) {
            return;
        }
        try {
            if (exam.isPublished()) {
                app.exams().unpublish(exam);
            } else {
                app.exams().publish(exam);
            }
            reload();
        } catch (ExamException ex) {
            UiHelper.showError("Cannot publish", ex.getMessage());
        }
    }

    private void deleteSelected() {
        Exam exam = requireSelection();
        if (exam == null) {
            return;
        }
        if (UiHelper.confirm("Delete exam", "Delete \"" + exam.getTitle() + "\" and its questions?")) {
            app.exams().deleteExam(exam);
            reload();
        }
    }

    private void viewResults() {
        Exam exam = requireSelection();
        if (exam == null) {
            return;
        }
        List<Submission> submissions = app.exams().getSubmissionsForExam(exam.getId());
        if (submissions.isEmpty()) {
            UiHelper.showInfo("No submissions yet",
                    "No student has attempted \"" + exam.getTitle() + "\" so far.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        double sum = 0;
        for (Submission s : submissions) {
            User student = app.users().findById(s.getStudentId());
            String name = student == null ? s.getStudentId() : student.getFullName();
            var r = s.getResult();
            sb.append(String.format("%-24s %5.1f%%  %s%n",
                    name, r.getPercentage(), r.getLetterGrade()));
            sum += r.getPercentage();
        }
        sb.append(String.format("%nSubmissions: %d   Class average: %.1f%%",
                submissions.size(), sum / submissions.size()));
        UiHelper.showInfo("Results — " + exam.getTitle(), sb.toString());
    }

    /** Renders each exam row with its status, question count and total marks. */
    private static class ExamCell extends ListCell<Exam> {
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

            Label meta = new Label(String.format(
                    "%d question(s) · %.0f marks · %d min",
                    exam.getQuestionCount(), exam.getTotalMarks(), exam.getDurationMinutes()));
            meta.getStyleClass().add("list-meta");

            Label status = new Label(exam.isPublished() ? "PUBLISHED" : "DRAFT");
            status.getStyleClass().add(exam.isPublished() ? "badge-published" : "badge-draft");

            HBox row = new HBox(12, new VBox(2, title, meta));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row.getChildren().addAll(spacer, status);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 4, 6, 4));
            setGraphic(row);
            setText(null);
        }
    }
}
