package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.dto.tm.RepairJobTM;
import lk.ijse.etecmanagementsystem.model.RepairJobModel;
import java.sql.SQLException;

public class RepairCheckoutController {

    // --- UI ELEMENTS ---
    @FXML private Label lblJobId;
    @FXML private Label lblCustomer;
    @FXML private Label lblPartsTotal;
    @FXML private Label lblLaborTotal;
    @FXML private Label lblGrandTotal;
    @FXML private Label lblBalance;

    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private TextField txtAmountPaid;

    // --- DATA ---
    private RepairJobTM jobTM;
    private RepairDashboardController mainController;
    private final RepairJobModel repairModel = new RepairJobModel();

    private double grandTotal = 0.0;

    @FXML
    public void initialize() {
        // Setup Payment Methods
        cmbPaymentMethod.setItems(FXCollections.observableArrayList("CASH", "CARD", "TRANSFER"));
        cmbPaymentMethod.getSelectionModel().selectFirst();

        // Add Listener to calculate Balance/Due in real-time
        txtAmountPaid.textProperty().addListener((obs, oldVal, newVal) -> calculateBalance());
    }

    // --- RECEIVE DATA FROM DASHBOARD ---
    public void setInvoiceData(RepairJobTM job, RepairDashboardController main) {
        this.jobTM = job;
        this.mainController = main;

        // Display Basic Info
        lblJobId.setText("#" + job.getRepairId());
        lblCustomer.setText(job.getCustomerName());

        // Display Financials
        // NOTE: Ensure your TM/DTO has these values updated!
        double labor = job.getOriginalDto().getLaborCost();
        double parts = job.getOriginalDto().getPartsCost();
        this.grandTotal = job.getOriginalDto().getTotalAmount();

        lblPartsTotal.setText(String.format("%.2f", parts));
        lblLaborTotal.setText(String.format("%.2f", labor));
        lblGrandTotal.setText(String.format("%.2f", grandTotal));

        // Default: Assume they pay full amount
        txtAmountPaid.setText(String.valueOf(grandTotal));
    }

    private void calculateBalance() {
        try {
            String text = txtAmountPaid.getText();
            if (text.isEmpty()) {
                lblBalance.setText("Due: " + grandTotal);
                lblBalance.setStyle("-fx-text-fill: red;");
                return;
            }

            double paid = Double.parseDouble(text);
            double balance = paid - grandTotal;

            if (balance >= 0) {
                // Change to give back
                lblBalance.setText("Change: " + String.format("%.2f", balance));
                lblBalance.setStyle("-fx-text-fill: green;");
            } else {
                // Still owing money (Partial)
                lblBalance.setText("Due: " + String.format("%.2f", Math.abs(balance)));
                lblBalance.setStyle("-fx-text-fill: red;");
            }

        } catch (NumberFormatException e) {
            lblBalance.setText("Invalid Amount");
        }
    }

    @FXML
    private void handleConfirm() {
        try {
            double paid = Double.parseDouble(txtAmountPaid.getText());
            if (paid < 0) throw new NumberFormatException();

            String method = cmbPaymentMethod.getValue();
            int userId = 1; // Replace with LoginUtil.getUserId();
            int cusId = jobTM.getOriginalDto().getCusId();

            // CALL MODEL TRANSACTION
            boolean success = repairModel.completeCheckout(
                    jobTM.getRepairId(),
                    cusId,
                    userId,
                    grandTotal,
                    paid,
                    method
            );

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Job Delivered Successfully!").show();
                mainController.refreshList(); // Reload Dashboard
                closeWindow();
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid amount.").show();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) lblJobId.getScene().getWindow()).close();
    }
}