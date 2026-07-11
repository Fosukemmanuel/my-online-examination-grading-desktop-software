package com.ucc.oegs.ui;

import com.ucc.oegs.OegsApp;
import com.ucc.oegs.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Administrator screen for reviewing and removing user accounts.
 *
 * <p>Shows every registered account in a table and lets the admin delete any
 * account other than their own.</p>
 */
public class AdminDashboard {

    private final OegsApp app;
    private final TableView<User> table = new TableView<>();

    public AdminDashboard(OegsApp app) {
        this.app = app;
    }

    public Region build() {
        VBox content = new VBox(16);

        content.getChildren().add(UiHelper.heading("User Management"));
        content.getChildren().add(UiHelper.subheading(
                "All registered accounts across the system"));

        buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("ghost-button");
        refresh.setOnAction(e -> reload());

        Button delete = new Button("Delete selected account");
        delete.getStyleClass().add("danger-button");
        delete.setOnAction(e -> deleteSelected());

        HBox actions = new HBox(10, refresh, delete);
        content.getChildren().addAll(table, actions);

        reload();
        return Chrome.page(app, "Administrator Dashboard", content);
    }

    private void buildTable() {
        TableColumn<User, String> name = new TableColumn<>("Full name");
        name.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        name.setPrefWidth(220);

        TableColumn<User, String> username = new TableColumn<>("Username");
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        username.setPrefWidth(140);

        TableColumn<User, String> role = new TableColumn<>("Role");
        role.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getRole().getDisplayName()));
        role.setPrefWidth(140);

        TableColumn<User, String> email = new TableColumn<>("Email");
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        email.setPrefWidth(240);

        table.getColumns().add(name);
        table.getColumns().add(username);
        table.getColumns().add(role);
        table.getColumns().add(email);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new javafx.scene.control.Label("No accounts found."));
    }

    private void reload() {
        table.setItems(FXCollections.observableArrayList(app.users().getAllUsers()));
    }

    private void deleteSelected() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelper.showError("No selection", "Please select an account to delete.");
            return;
        }
        if (selected.equals(app.auth().getCurrentUser())) {
            UiHelper.showError("Not allowed", "You cannot delete the account you are signed in with.");
            return;
        }
        boolean ok = UiHelper.confirm("Delete account",
                "Permanently delete " + selected.getFullName() + "'s account?");
        if (ok) {
            app.users().deleteUser(selected);
            reload();
        }
    }
}
