package com.ucc.oegs.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Region;

/**
 * Small collection of shared UI helpers: consistent alert dialogs and a couple
 * of styling shortcuts used across every screen.
 */
public final class UiHelper {

    private UiHelper() {
    }

    public static void showError(String header, String message) {
        show(Alert.AlertType.ERROR, "Error", header, message);
    }

    public static void showInfo(String header, String message) {
        show(Alert.AlertType.INFORMATION, "Information", header, message);
    }

    public static boolean confirm(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please confirm");
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert.showAndWait()
                .filter(button -> button.getButtonData().isDefaultButton())
                .isPresent();
    }

    private static void show(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    public static Label heading(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("heading");
        return label;
    }

    public static Label subheading(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("subheading");
        return label;
    }

    /** Trims a text control's value, returning an empty string for null. */
    public static String textOf(TextInputControl control) {
        return control.getText() == null ? "" : control.getText().trim();
    }
}
