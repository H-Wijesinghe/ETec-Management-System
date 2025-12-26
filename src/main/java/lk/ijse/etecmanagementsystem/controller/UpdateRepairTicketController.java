package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.App;
import lk.ijse.etecmanagementsystem.dto.CustomerDTO;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.dto.tm.RepairJobTM;
import lk.ijse.etecmanagementsystem.model.CustomersModel; // Model Import
import lk.ijse.etecmanagementsystem.model.RepairJobModel; // Model Import

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class UpdateRepairTicketController {

    @FXML private Label lblJobId;

    // --- CUSTOMER SEARCH & PREVIEW ---
    @FXML private ComboBox<String> cmbCustomer;
    @FXML private Label lblCusName;
    @FXML private Label lblCusContact;
    @FXML private Label lblCusId;
    @FXML private Label lblCusEmail;
    @FXML private Label lblCusAddress;

    // --- DEVICE FIELDS ---
    @FXML private TextField txtDeviceName;
    @FXML private TextField txtSerial;
    @FXML private TextArea txtProblem;

    // Data handling
    private RepairJobTM currentJob;
    private RepairDashboardController mainController;
    private final Map<String, CustomerDTO> customerMap = new HashMap<>();
    private final ObservableList<String> originalList = FXCollections.observableArrayList();
    private int selectedCustomerId = -1;

    // --- MODELS ---
    private final CustomersModel customersModel = new CustomersModel();
    private final RepairJobModel repairJobModel = new RepairJobModel();

    // =========================================================
    // INITIALIZATION & DATA LOADING
    // =========================================================

    @FXML
    public void initialize() {
        loadCustomerData(); // Load list for search
        setupSearchFilter(); // Attach listener
    }

    public void setJobData(RepairJobTM job, RepairDashboardController mainController) {
        this.currentJob = job;
        this.mainController = mainController;

        lblJobId.setText("JOB #" + job.getRepairId());

        // 1. FILL DEVICE DETAILS
        txtDeviceName.setText(job.getDeviceName());
        txtSerial.setText(job.getSerialNumber());
        txtProblem.setText(job.getProblemDescription());

        // 2. FILL CURRENT CUSTOMER (PREVIEW)
        lblCusName.setText(job.getCustomerName());
        lblCusContact.setText(job.getContactNumber());

        // Fill extra details (Email/Address) by looking up the ID
        if (job.getOriginalDto() != null) {
            selectedCustomerId = job.getOriginalDto().getCusId();
            lblCusId.setText(String.valueOf(selectedCustomerId));

            // Populate Email/Address from loaded map
            populateCustomerDetailsById(selectedCustomerId);

            // Set ComboBox Text to current name
            cmbCustomer.getEditor().setText(job.getCustomerName());

        }
    }

    private void populateCustomerDetailsById(int cusId) {
        // Iterate through loaded customers to find the matching ID for display
        for (CustomerDTO dto : customerMap.values()) {
            if (dto.getId() == cusId) {
                lblCusEmail.setText(dto.getEmailAddress());
                lblCusAddress.setText(dto.getAddress());
                break;
            }
        }
    }

    // =========================================================
    // SEARCH LOGIC
    // =========================================================

    private void loadCustomerData() {
        customerMap.clear();
        originalList.clear();

        try {
            // FETCH REAL DATA FROM DB
            List<CustomerDTO> customers = customersModel.getAllCustomers();

            for (CustomerDTO customer : customers) {
                String key = customer.getName() + " | " + customer.getNumber();
                customerMap.put(key, customer);
                originalList.add(key);
            }
            cmbCustomer.setItems(FXCollections.observableArrayList(originalList));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load customers.");
        }
    }

    private void setupSearchFilter() {
        cmbCustomer.getEditor().textProperty().addListener((obs, old, newVal) -> {

            if (newVal == null) return;

            if (cmbCustomer.getSelectionModel().getSelectedItem() != null &&
                    Objects.equals(cmbCustomer.getSelectionModel().getSelectedItem(), newVal)) {
                return;
            }

            cmbCustomer.getSelectionModel().clearSelection();


            ObservableList<String> filteredList = originalList.stream()
                    .filter(item -> item.toLowerCase().contains(newVal.trim().toLowerCase()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            if (!filteredList.isEmpty()) {
                cmbCustomer.setItems(filteredList);

                cmbCustomer.getEditor().setText(newVal);
                cmbCustomer.getEditor().positionCaret(newVal.length());

                if (!cmbCustomer.isShowing()) {
                    cmbCustomer.show();
                }
            } else {

                cmbCustomer.hide();
            }
        });


        cmbCustomer.setOnAction(e -> {
            String key = cmbCustomer.getSelectionModel().getSelectedItem();

            if (key == null) key = cmbCustomer.getEditor().getText();

            if (key != null && customerMap.containsKey(key)) {
                handleCustomerSelection();
            }
        });
    }

    @FXML
    private void handleCustomerSelection() {
        String selectedKey = cmbCustomer.getSelectionModel().getSelectedItem();

        if (selectedKey != null && customerMap.containsKey(selectedKey)) {
            CustomerDTO selectedCus = customerMap.get(selectedKey);

            selectedCustomerId = selectedCus.getId();
            lblCusName.setText(selectedCus.getName());
            lblCusContact.setText(selectedCus.getNumber());
            lblCusId.setText(String.valueOf(selectedCus.getId()));
            lblCusAddress.setText(selectedCus.getAddress());
            lblCusEmail.setText(selectedCus.getEmailAddress());
        }
    }

    // =========================================================
    // BUTTON ACTIONS
    // =========================================================

    @FXML
    private void handleAddNewCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Customers.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh search list
            loadCustomerData();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomerId == -1 || txtDeviceName.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation", "Customer and Device are required.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Update details for Job #" + currentJob.getRepairId() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() != ButtonType.YES) {
            return;
        }

        try {
            RepairJobDTO jobDTO = new RepairJobDTO();

            // Set ID so DB knows which row to update
            jobDTO.setRepairId(currentJob.getRepairId());

            // Set Updated Fields
            jobDTO.setCusId(selectedCustomerId);
            jobDTO.setDeviceName(txtDeviceName.getText());
            jobDTO.setDeviceSn(txtSerial.getText());
            jobDTO.setProblemDesc(txtProblem.getText());

            // CALL MODEL
            boolean success = repairJobModel.updateRepairJob(jobDTO);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Details Updated Successfully.");
                mainController.refreshList(); // Reload main dashboard
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update record.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete Job #" + currentJob.getRepairId() + "?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // CALL MODEL
                boolean success = repairJobModel.deleteRepairJob(currentJob.getRepairId());

                if (success) {
                    mainController.refreshList();
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete record.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }
        }
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        Stage stage = (Stage) lblJobId.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }
}