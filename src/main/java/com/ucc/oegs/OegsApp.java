package com.ucc.oegs;

import com.ucc.oegs.model.Role;
import com.ucc.oegs.model.User;
import com.ucc.oegs.persistence.DataStore;
import com.ucc.oegs.service.AuthService;
import com.ucc.oegs.service.ExamService;
import com.ucc.oegs.service.GradingService;
import com.ucc.oegs.service.UserService;
import com.ucc.oegs.ui.AdminDashboard;
import com.ucc.oegs.ui.InstructorDashboard;
import com.ucc.oegs.ui.LoginView;
import com.ucc.oegs.ui.StudentDashboard;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX {@link Application} that wires the services together and acts as the
 * central navigator: every screen calls back here to swap the window's content.
 *
 * <p>Keeping navigation in one place means the individual views stay focused on
 * their own layout and never need to know how to construct one another beyond
 * asking the app to "show" the next screen.</p>
 */
public class OegsApp extends Application {

    private static final double WIDTH = 1024;
    private static final double HEIGHT = 720;

    private Stage stage;
    private Scene scene;

    // Services shared across the whole application.
    private DataStore dataStore;
    private AuthService authService;
    private UserService userService;
    private ExamService examService;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // Compose the object graph once at start-up.
        this.dataStore = new DataStore();
        this.userService = new UserService(dataStore);
        this.authService = new AuthService(dataStore);
        this.examService = new ExamService(dataStore, new GradingService());

        // Ensure there is always at least one account of each role to sign in with.
        userService.seedDefaultsIfEmpty();

        this.scene = new Scene(new LoginView(this).build(), WIDTH, HEIGHT);
        applyStylesheet();

        stage.setTitle("Developed by Emmanuel Fosu (MS/ITE/25/0045) — Online Examination and Grading System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(640);
        stage.show();
    }

    /** Replaces the whole window content with the supplied view root. */
    public void setContent(Parent root) {
        scene.setRoot(root);
    }

    public void showLogin() {
        authService.logout();
        setContent(new LoginView(this).build());
    }

    /** Routes a freshly authenticated user to the dashboard for their role. */
    public void showDashboardFor(User user) {
        Parent dashboard;
        if (user.getRole() == Role.ADMINISTRATOR) {
            dashboard = new AdminDashboard(this).build();
        } else if (user.getRole() == Role.INSTRUCTOR) {
            dashboard = new InstructorDashboard(this).build();
        } else {
            dashboard = new StudentDashboard(this).build();
        }
        setContent(dashboard);
    }

    private void applyStylesheet() {
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    // ----- Service accessors used by the views -------------------------------

    public AuthService auth() {
        return authService;
    }

    public UserService users() {
        return userService;
    }

    public ExamService exams() {
        return examService;
    }

    public Stage stage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
