package lk.ijse.etecmanagementsystem.controller;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.dto.tm.ManualTransactionResult;

import java.util.function.Consumer;

public class ManualTransactionController {

    @FXML private ComboBox<String> cmbType;
    @FXML private TextField txtAmount;
    @FXML private ComboBox<String> cmbMethod;
    @FXML private TextField txtNote;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    // This callback sends data back to the main window
    private Consumer<ManualTransactionResult> saveHandler;

    @FXML
    public void initialize() {
        // 1. Populate ComboBoxes
        cmbType.setItems(FXCollections.observableArrayList("EXPENSE", "SUPPLIER_PAYMENT", "OTHER_INCOME"));
        cmbType.getSelectionModel().select("EXPENSE");

        cmbMethod.setItems(FXCollections.observableArrayList("CASH", "CARD", "TRANSFER"));
        cmbMethod.getSelectionModel().select("CASH");

        // 2. Add Validation Listener
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    // Call this from your Main Controller to tell this popup what to do on save
    public void setOnSave(Consumer<ManualTransactionResult> handler) {
        this.saveHandler = handler;
    }

    private void validateForm() {
        boolean isValid = false;
        String amountText = txtAmount.getText().trim();

        if (!amountText.isEmpty()) {
            try {
                Double.parseDouble(amountText);
                isValid = true;
            } catch (NumberFormatException e) {
                // Not a number
            }
        }
        btnSave.setDisable(!isValid);
    }

    @FXML
    private void handleSave() {
        if (saveHandler != null) {
            // Create the result object
            ManualTransactionResult result = new ManualTransactionResult(
                    cmbType.getValue(),
                    Double.parseDouble(txtAmount.getText()),
                    cmbMethod.getValue(),
                    txtNote.getText()
            );

            // Pass it back to main controller
            saveHandler.accept(result);

            // Close window
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}