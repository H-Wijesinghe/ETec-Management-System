package lk.ijse.etecmanagementsystem.service;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class NavigationHelper {

    /**
     * Highlights the correct button and un-highlights the others.
     * Call this in the initialize() method of every controller.
     */
    public static void highlightActiveButton(List<Button> allButtons, Button activeButton) {
        for (Button btn : allButtons) {
            // Remove 'active' class from everyone
            btn.getStyleClass().remove("active");

            // Ensure the base class is there (in case it was missed in FXML)
            if (!btn.getStyleClass().contains("nav-button")) {
                btn.getStyleClass().add("nav-button");
            }
        }

        // Add 'active' class to the current button
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }

    /**
     * Switch the scene on the current stage.
     */
    public static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Get the stage from the event source (the button clicked)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Preserve the current window size (optional but recommended)
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root);
            // Re-add your CSS (if not added in FXML)
            scene.getStylesheets().add(NavigationHelper.class.getResource("/style.css").toExternalForm());

            stage.setScene(scene);

            // Restore size if needed
             stage.setWidth(width);
             stage.setHeight(height);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + fxmlPath);
        }
    }
}