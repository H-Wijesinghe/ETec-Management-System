package lk.ijse.etecmanagementsystem.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.App;

import java.io.IOException;

public class Category {

    private static final ObservableList<String> categories;

    static {
        categories = FXCollections.observableArrayList();
    }


    public static void setCategoryStage(ComboBox<String> cmbCategory) {
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Category");
        try {
            newStage.setScene(new Scene(App.loadFXML("category"), 400, 200));
            newStage.showAndWait();
            newStage.setResizable(false);
            // After closing, refresh categories
            ObservableList<String> updatedCategories = FXCollections.observableArrayList(categories);
            cmbCategory.setItems(updatedCategories);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Category window: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static ObservableList<String> getCategories() {
        return categories;
    }

}
