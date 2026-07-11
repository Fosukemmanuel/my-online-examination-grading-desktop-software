package com.ucc.oegs.ui;

import com.ucc.oegs.OegsApp;
import com.ucc.oegs.exception.AuthenticationException;
import com.ucc.oegs.exception.ValidationException;
import com.ucc.oegs.model.Role;
import com.ucc.oegs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The sign-in screen, also offering an inline registration form.
 *
 * <p>Demonstrates event-driven programming (button/enter-key handlers),
 * exception handling (catching {@link AuthenticationException} /
 * {@link ValidationException} to show friendly messages) and input validation.</p>
 */
public class LoginView {

    private final OegsApp app;

    public LoginView(OegsApp app) {
        this.app = app;
    }

    public Parent build() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(420);
        card.setPadding(new Insets(32));
        card.setAlignment(Pos.CENTER);

        Label title = new Label("Online Examination\n& Grading System");
        title.getStyleClass().add("brand");
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);

        Label subtitle = new Label("Developed by Emmanuel Fosu (MS/ITE/25/0045)");
        subtitle.getStyleClass().add("subheading");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label message = new Label();
        message.getStyleClass().add("error-text");
        message.setWrapText(true);

        Button loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Runnable attemptLogin = () -> {
            message.setText("");
            try {
                User user = app.auth().login(
                        UiHelper.textOf(usernameField), passwordField.getText());
                app.showDashboardFor(user);
            } catch (AuthenticationException ex) {
                message.setText(ex.getMessage());
            }
        };

        loginButton.setOnAction(e -> attemptLogin.run());
        passwordField.setOnAction(e -> attemptLogin.run()); // Enter key submits

        Button registerButton = new Button("Create an account");
        registerButton.getStyleClass().add("link-button");
        registerButton.setOnAction(e -> app.setContent(buildRegister()));

        Label hint = new Label("Demo logins — admin/admin123 · instructor/teach123 · student/study123");
        hint.getStyleClass().add("hint");
        hint.setWrapText(true);
        hint.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, subtitle, usernameField, passwordField,
                loginButton, message, registerButton, hint);

        return centered(card);
    }

    private Parent buildRegister() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setMaxWidth(460);
        card.setPadding(new Insets(28));

        card.getChildren().add(UiHelper.heading("Create an account"));

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(Role.values());
        roleBox.getSelectionModel().select(Role.STUDENT);
        roleBox.setMaxWidth(Double.MAX_VALUE);

        TextField fullName = new TextField();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        TextField email = new TextField();
        TextField extra1 = new TextField();
        TextField extra2 = new TextField();

        Label extra1Label = new Label();
        Label extra2Label = new Label();

        // The two optional fields change meaning depending on the chosen role.
        Runnable refreshExtras = () -> {
            Role role = roleBox.getValue();
            switch (role) {
                case STUDENT -> {
                    extra1Label.setText("Index number");
                    extra2Label.setText("Programme");
                    extra1.setVisible(true); extra1.setManaged(true);
                    extra2.setVisible(true); extra2.setManaged(true);
                    extra1Label.setVisible(true); extra1Label.setManaged(true);
                    extra2Label.setVisible(true); extra2Label.setManaged(true);
                }
                case INSTRUCTOR -> {
                    extra1Label.setText("Department");
                    extra1.setVisible(true); extra1.setManaged(true);
                    extra1Label.setVisible(true); extra1Label.setManaged(true);
                    extra2.setVisible(false); extra2.setManaged(false);
                    extra2Label.setVisible(false); extra2Label.setManaged(false);
                }
                case ADMINISTRATOR -> {
                    extra1.setVisible(false); extra1.setManaged(false);
                    extra2.setVisible(false); extra2.setManaged(false);
                    extra1Label.setVisible(false); extra1Label.setManaged(false);
                    extra2Label.setVisible(false); extra2Label.setManaged(false);
                }
            }
        };
        roleBox.setOnAction(e -> refreshExtras.run());
        refreshExtras.run();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        int r = 0;
        grid.addRow(r++, new Label("Role"), roleBox);
        grid.addRow(r++, new Label("Full name"), fullName);
        grid.addRow(r++, new Label("Username"), username);
        grid.addRow(r++, new Label("Password"), password);
        grid.addRow(r++, new Label("Email"), email);
        grid.addRow(r++, extra1Label, extra1);
        grid.addRow(r++, extra2Label, extra2);
        for (var node : grid.getChildren()) {
            if (node instanceof TextField tf) {
                tf.setMaxWidth(Double.MAX_VALUE);
            }
        }

        Label message = new Label();
        message.getStyleClass().add("error-text");
        message.setWrapText(true);

        Button create = new Button("Register");
        create.getStyleClass().add("primary-button");
        create.setOnAction(e -> {
            message.setText("");
            try {
                app.users().register(roleBox.getValue(),
                        UiHelper.textOf(fullName), UiHelper.textOf(username),
                        password.getText(), UiHelper.textOf(email),
                        UiHelper.textOf(extra1), UiHelper.textOf(extra2));
                UiHelper.showInfo("Account created",
                        "You can now sign in with your new credentials.");
                app.showLogin();
            } catch (ValidationException ex) {
                message.setText(ex.getMessage());
            }
        });

        Button back = new Button("Back to sign in");
        back.getStyleClass().add("link-button");
        back.setOnAction(e -> app.showLogin());

        HBox actions = new HBox(10, create, back);
        actions.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(grid, message, actions);
        return centered(card);
    }

    private Parent centered(VBox card) {
        StackPane root = new StackPane(card);
        root.getStyleClass().add("app-background");
        root.setPadding(new Insets(24));
        return root;
    }
}
