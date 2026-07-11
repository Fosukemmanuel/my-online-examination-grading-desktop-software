package com.ucc.oegs.ui;

import com.ucc.oegs.OegsApp;
import com.ucc.oegs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Builds the common page frame shared by every dashboard: a branded top bar
 * showing the signed-in user with a logout action, and a content area below.
 */
public final class Chrome {

    private Chrome() {
    }

    public static BorderPane page(OegsApp app, String title, Region content) {
        User user = app.auth().getCurrentUser();

        Label brand = new Label(title);
        brand.getStyleClass().add("topbar-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label who = new Label(user == null ? "" : user.getFullName() + "  ·  " + user.getRole());
        who.getStyleClass().add("topbar-user");

        Button logout = new Button("Log out");
        logout.getStyleClass().addAll("ghost-button", "topbar-logout");
        logout.setOnAction(e -> app.showLogin());

        HBox topBar = new HBox(16, brand, spacer, who, logout);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14, 24, 14, 24));
        topBar.getStyleClass().add("topbar");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setTop(topBar);
        content.setPadding(new Insets(24));
        root.setCenter(content);
        return root;
    }
}
