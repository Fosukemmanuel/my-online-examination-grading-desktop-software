package com.ucc.oegs.ui;

import com.ucc.oegs.OegsApp;
import com.ucc.oegs.exception.ValidationException;
import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.MultipleChoiceQuestion;
import com.ucc.oegs.model.Question;
import com.ucc.oegs.model.ShortAnswerQuestion;
import com.ucc.oegs.model.TrueFalseQuestion;
import com.ucc.oegs.model.User;
import com.ucc.oegs.util.IdGenerator;
import com.ucc.oegs.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Modal window for authoring an exam: first its metadata, then any number of
 * questions of three different kinds.
 *
 * <p>The answer-key controls swap dynamically with the selected question type,
 * and each "Add" click constructs the matching {@link Question} subclass —
 * the point at which the model's polymorphism is put to work.</p>
 */
public class ExamBuilderView {

    private final OegsApp app;
    private final Runnable onSaved;
    private final Stage stage = new Stage();

    private Exam exam; // null until the metadata form is completed / an existing exam is loaded
    private final ListView<Question> questionList = new ListView<>();

    public ExamBuilderView(OegsApp app, Runnable onSaved) {
        this.app = app;
        this.onSaved = onSaved;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Exam Builder");
    }

    /** Opens the builder starting with the "create exam" metadata form. */
    public void open() {
        stage.setScene(new Scene(buildMetadataForm(), 640, 560));
        applyStyles();
        stage.showAndWait();
    }

    /** Opens the builder for an existing exam, jumping straight to question editing. */
    public void openFor(Exam existing) {
        this.exam = existing;
        stage.setScene(new Scene(buildQuestionEditor(), 720, 640));
        applyStyles();
        stage.showAndWait();
    }

    private void applyStyles() {
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            stage.getScene().getStylesheets().add(css.toExternalForm());
        }
    }

    // ----- Step 1: exam metadata --------------------------------------------

    private VBox buildMetadataForm() {
        TextField title = new TextField();
        TextField course = new TextField();
        TextField duration = new TextField("30");
        TextArea description = new TextArea();
        description.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Exam title"), title);
        grid.addRow(1, new Label("Course code"), course);
        grid.addRow(2, new Label("Duration (minutes)"), duration);
        grid.addRow(3, new Label("Description"), description);
        for (var node : grid.getChildren()) {
            if (node instanceof TextField tf) {
                tf.setMaxWidth(Double.MAX_VALUE);
            }
        }

        Label error = new Label();
        error.getStyleClass().add("error-text");

        Button next = new Button("Create & add questions");
        next.getStyleClass().add("primary-button");
        next.setOnAction(e -> {
            try {
                int mins = Validator.parseInt(duration.getText(), "Duration");
                User me = app.auth().getCurrentUser();
                exam = app.exams().createExam(UiHelper.textOf(title), UiHelper.textOf(course),
                        UiHelper.textOf(description), mins, me.getId());
                stage.getScene().setRoot(buildQuestionEditor());
            } catch (ValidationException ex) {
                error.setText(ex.getMessage());
            }
        });

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("ghost-button");
        cancel.setOnAction(e -> stage.close());

        VBox box = new VBox(16,
                UiHelper.heading("New Examination"),
                grid, error, new HBox(10, next, cancel));
        box.setPadding(new Insets(24));
        box.getStyleClass().add("app-background");
        return box;
    }

    // ----- Step 2: questions -------------------------------------------------

    private VBox buildQuestionEditor() {
        questionList.setItems(FXCollections.observableArrayList(exam.getQuestions()));

        // --- question type + shared fields ---
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Multiple Choice", "True / False", "Short Answer");
        typeBox.getSelectionModel().selectFirst();

        TextArea questionText = new TextArea();
        questionText.setPromptText("Question text");
        questionText.setPrefRowCount(2);

        TextField marks = new TextField("1");

        // --- type-specific answer key area ---
        VBox keyArea = new VBox(8);
        rebuildKeyArea(keyArea, typeBox.getValue());
        typeBox.setOnAction(e -> rebuildKeyArea(keyArea, typeBox.getValue()));

        Label error = new Label();
        error.getStyleClass().add("error-text");

        Button add = new Button("Add question");
        add.getStyleClass().add("primary-button");
        add.setOnAction(e -> {
            error.setText("");
            try {
                Question q = buildQuestion(typeBox.getValue(),
                        UiHelper.textOf(questionText), marks.getText(), keyArea);
                app.exams().addQuestion(exam, q);
                questionList.getItems().add(q);
                questionText.clear();
                rebuildKeyArea(keyArea, typeBox.getValue());
            } catch (ValidationException ex) {
                error.setText(ex.getMessage());
            }
        });

        Button removeQ = new Button("Remove selected");
        removeQ.getStyleClass().add("ghost-button");
        removeQ.setOnAction(e -> {
            Question selected = questionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                app.exams().removeQuestion(exam, selected);
                questionList.getItems().remove(selected);
            }
        });

        Button done = new Button("Save & close");
        done.getStyleClass().add("primary-button");
        done.setOnAction(e -> {
            onSaved.run();
            stage.close();
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Type"), typeBox);
        form.addRow(1, new Label("Question"), questionText);
        form.addRow(2, new Label("Marks"), marks);
        marks.setMaxWidth(120);
        typeBox.setMaxWidth(Double.MAX_VALUE);
        questionText.setMaxWidth(Double.MAX_VALUE);

        VBox editor = new VBox(10,
                UiHelper.subheading("Add a question"),
                form, keyArea, error,
                new HBox(10, add, removeQ));

        VBox listBox = new VBox(8,
                UiHelper.subheading("Questions on this paper"),
                questionList);
        VBox.setVgrow(questionList, Priority.ALWAYS);

        VBox box = new VBox(16,
                UiHelper.heading(exam.getCourseCode() + " — " + exam.getTitle()),
                editor, listBox, new HBox(10, done));
        box.setPadding(new Insets(24));
        box.getStyleClass().add("app-background");
        VBox.setVgrow(listBox, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        VBox wrapper = new VBox(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        wrapper.getStyleClass().add("app-background");
        return wrapper;
    }

    /** Swaps the answer-key controls in {@code keyArea} to match the chosen type. */
    private void rebuildKeyArea(VBox keyArea, String type) {
        keyArea.getChildren().clear();
        switch (type) {
            case "Multiple Choice" -> {
                keyArea.getChildren().add(new Label("Options (one per line). Mark the correct one's number below."));
                TextArea options = new TextArea();
                options.setPromptText("Option 1\nOption 2\nOption 3\nOption 4");
                options.setPrefRowCount(4);
                options.setId("mcq-options");
                TextField correct = new TextField("1");
                correct.setId("mcq-correct");
                correct.setMaxWidth(160);
                keyArea.getChildren().addAll(options,
                        new HBox(8, new Label("Correct option number:"), correct));
            }
            case "True / False" -> {
                ComboBox<String> answer = new ComboBox<>();
                answer.getItems().addAll("True", "False");
                answer.getSelectionModel().selectFirst();
                answer.setId("tf-answer");
                keyArea.getChildren().add(new HBox(8, new Label("Correct answer:"), answer));
            }
            case "Short Answer" -> {
                TextField accepted = new TextField();
                accepted.setPromptText("Accepted answers separated by | e.g. encapsulation|data hiding");
                accepted.setId("sa-accepted");
                keyArea.getChildren().addAll(new Label("Accepted answer(s):"), accepted);
            }
            default -> { /* nothing */ }
        }
    }

    /** Constructs the concrete {@link Question} subclass for the selected type. */
    private Question buildQuestion(String type, String text, String marksRaw, VBox keyArea)
            throws ValidationException {
        Validator.requireNonBlank(text, "Question text");
        double marks = Validator.parseDouble(marksRaw, "Marks");
        Validator.requirePositive(marks, "Marks");
        String id = IdGenerator.questionId();

        return switch (type) {
            case "Multiple Choice" -> {
                TextArea options = (TextArea) keyArea.lookup("#mcq-options");
                TextField correct = (TextField) keyArea.lookup("#mcq-correct");
                var opts = new java.util.ArrayList<String>();
                for (String line : options.getText().split("\\r?\\n")) {
                    if (!line.isBlank()) {
                        opts.add(line.trim());
                    }
                }
                if (opts.size() < 2) {
                    throw new ValidationException("Provide at least two options.");
                }
                int oneBased = Validator.parseInt(correct.getText(), "Correct option number");
                if (oneBased < 1 || oneBased > opts.size()) {
                    throw new ValidationException("Correct option number must be between 1 and " + opts.size() + ".");
                }
                yield new MultipleChoiceQuestion(id, text, marks, opts, oneBased - 1);
            }
            case "True / False" -> {
                @SuppressWarnings("unchecked")
                ComboBox<String> answer = (ComboBox<String>) keyArea.lookup("#tf-answer");
                boolean correct = "True".equals(answer.getValue());
                yield new TrueFalseQuestion(id, text, marks, correct);
            }
            case "Short Answer" -> {
                TextField accepted = (TextField) keyArea.lookup("#sa-accepted");
                Validator.requireNonBlank(accepted.getText(), "Accepted answer(s)");
                yield new ShortAnswerQuestion(id, text, marks, accepted.getText().trim());
            }
            default -> throw new ValidationException("Unknown question type.");
        };
    }
}
