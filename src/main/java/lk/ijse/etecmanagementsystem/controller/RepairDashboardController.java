package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.App;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.util.PaymentStatus;
import lk.ijse.etecmanagementsystem.util.RepairStatus;
import lk.ijse.etecmanagementsystem.dto.tm.RepairJobTM;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class RepairDashboardController {

    // =========================================================
    // FXML INJECTIONS (Must match RepairMain.fxml IDs exactly)
    // =========================================================

    // Top & Search
    @FXML private TextField txtSearch;
    @FXML private ComboBox<RepairStatus> comboStatusFilter;
    @FXML private Button btnAddTicket;

    // List View
    @FXML private ListView<RepairJobTM> listRepairJobs;

    // Right Side Details Pane
    @FXML private VBox detailsPane;

    // Header Labels
    @FXML private Label lblJobId;
    @FXML private Label lblStatusBadge;
    @FXML private Label lblDate;

    // Customer & Device Details
    @FXML private Label lblCustomerName;
    @FXML private Label lblContact;
    @FXML private Label lblDeviceName;
    @FXML private Label lblSerial;

    // Workflow
    @FXML private ProgressBar progressWorkflow;

    // Tabs (3 Stages)
    @FXML private TextArea txtIntake;     // Matches <TextArea fx:id="txtIntake"> in Tab 1
    @FXML private TextArea txtDiagnosis;  // Matches Tab 2
    @FXML private TextArea txtResolution; // Matches Tab 3

    // Parts Table (Placeholder for now)
    @FXML private TableView<?> tblParts;
    @FXML private TableColumn<?, ?> colPartName;
    @FXML private TableColumn<?, ?> colPartPrice;
    @FXML private TableColumn<?, ?> colPartQty;

    @FXML private VBox cardCustomer;
    @FXML private VBox cardDevice;
    @FXML private Button btnUpdateJob;

    // =========================================================
    // DATA & INITIALIZATION
    // =========================================================

    private ObservableList<RepairJobTM> masterData = FXCollections.observableArrayList();
    private FilteredList<RepairJobTM> filteredData;
    private RepairJobTM currentSelection;

    @FXML
    public void initialize() {
        // 1. Setup Status Filter
        comboStatusFilter.getItems().setAll(RepairStatus.values());

        // 2. Setup List View Appearance
        setupListView();

        // 3. Load Mock Data (Simulating DB)
        loadMockData();

        // 4. Setup Filtering Logic
        filteredData = new FilteredList<>(masterData, p -> true);
        listRepairJobs.setItems(filteredData);
        setupListeners();
    }

    // =========================================================
    // NEW: HANDLE CARD CLICKS (UPDATE)
    // =========================================================

    @FXML
    private void handleCardClick(MouseEvent event) {
        if (currentSelection == null) return;

        // CONSTRAINT CHECK: Only PENDING allowed
        if (currentSelection.getStatus() != RepairStatus.PENDING) {
            showAlert(Alert.AlertType.WARNING, "Restricted",
                    "You can only edit details for PENDING jobs.\n" +
                            "Current status: " + currentSelection.getStatus());
            return;
        }

        openUpdateWindow();
    }

    @FXML
    private void handleUpdateJob() {
        // Double check constraints (Safety)
        if (currentSelection == null || currentSelection.getStatus() != RepairStatus.PENDING) {
            return;
        }
        openUpdateWindow();
    }

    private void openUpdateWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/UpdateRepairTicket.fxml"));
            Parent root = loader.load();

            UpdateRepairTicketController controller = loader.getController();
            controller.setJobData(currentSelection, this);

            Stage stage = new Stage();
            stage.setTitle("Update Job #" + currentSelection.repairIdProperty().get());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Blocks main window (Resource safe)
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open update window.");
        }
    }

    private void setupListeners() {
        // Filter Search Text
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filterList());

        // Filter Status Combo
        comboStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterList());

        // Selection Listener (Show details when clicked)
        listRepairJobs.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showDetails(newVal);
            } else {
                detailsPane.setVisible(false);
            }
        });
    }

    // =========================================================
    // LOGIC IMPLEMENTATION
    // =========================================================

    private void filterList() {
        String filterText = txtSearch.getText().toLowerCase();
        RepairStatus statusFilter = comboStatusFilter.getValue();

        filteredData.setPredicate(job -> {
            boolean matchesText = job.customerNameProperty().get().toLowerCase().contains(filterText) ||
                    job.deviceNameProperty().get().toLowerCase().contains(filterText) ||
                    String.valueOf(job.repairIdProperty().get()).contains(filterText);

            boolean matchesStatus = (statusFilter == null) || job.getStatus() == statusFilter;

            return matchesText && matchesStatus;
        });
    }

    private void showDetails(RepairJobTM job) {
        this.currentSelection = job;
        detailsPane.setVisible(true);

        if (job.getStatus() == RepairStatus.PENDING) {
            btnUpdateJob.setDisable(false);
        } else {
            btnUpdateJob.setDisable(true);
        }

        // Bind Labels
        lblJobId.setText("Job #" + job.repairIdProperty().get());
        lblDate.setText(job.dateInFormattedProperty().get());

        lblCustomerName.setText(job.customerNameProperty().get());
        lblContact.setText(job.contactNumberProperty().get());

        lblDeviceName.setText(job.deviceNameProperty().get());
        lblSerial.setText(job.serialNumberProperty().get());

        // Bind Text Areas
        txtIntake.setText(job.problemDescriptionProperty().get());
        // NOTE: If you add 'diagnosis' and 'resolution' to DB/DTO later, bind them here.
        txtDiagnosis.setText("");
        txtResolution.setText("");

        // Update Status UI
        refreshStatusUI(job.getStatus());
    }

    // =========================================================
    // EVENT HANDLERS (Matches onAction in RepairMain.fxml)
    // =========================================================

    @FXML
    private void handleNewTicket() {
        try {
            // Load the Popup
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/AddRepairTicket.fxml"));
            Parent root = loader.load();

            // Setup Controller
            AddRepairTicketController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("New Repair Ticket");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load popup: " + e.getMessage());
        }
    }

    @FXML
    private void handleNextStatus() {
        if (currentSelection == null) return;

        RepairStatus current = currentSelection.getStatus();
        int nextOrdinal = current.ordinal() + 1;

        if (nextOrdinal < RepairStatus.values().length) {
            RepairStatus nextStatus = RepairStatus.values()[nextOrdinal];
            // Don't auto-move to Cancelled
            if (nextStatus != RepairStatus.CANCELLED) {
                updateStatus(nextStatus);
            }
        }
    }

    @FXML
    private void handlePrevStatus() {
        if (currentSelection == null) return;

        int prevOrdinal = currentSelection.getStatus().ordinal() - 1;
        if (prevOrdinal >= 0) {
            updateStatus(RepairStatus.values()[prevOrdinal]);
        }
    }

    @FXML
    private void handleUnclaimed() {
        if (currentSelection == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mark Unclaimed");
        alert.setHeaderText("Mark job #" + currentSelection.repairIdProperty().get() + " as Unclaimed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // You might want a specific enum for this, or just use CANCELLED for now
            updateStatus(RepairStatus.CANCELLED);
        }
    }

    @FXML
    private void handleAddPart() {
        if (currentSelection == null) return;
        showAlert(Alert.AlertType.INFORMATION, "Add Part", "Open Stock Selection Window Here.");
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    public void refreshList() {
        // Called by AddTicketController after saving
        loadMockData(); // Reload from DB in real app
        listRepairJobs.refresh();
    }

    private void updateStatus(RepairStatus newStatus) {
        // Update Model
        currentSelection.setStatus(newStatus);

        // Update UI
        refreshStatusUI(newStatus);
        listRepairJobs.refresh(); // Updates the text in the list on the left

        // TODO: Call DAO to update Database
        // repairModel.updateStatus(currentSelection.getRepairId(), newStatus);
    }

    private void refreshStatusUI(RepairStatus status) {
        lblStatusBadge.setText(status.toString());

        // Clean old styles
        lblStatusBadge.getStyleClass().removeAll("status-pending", "status-warn", "status-done", "status-danger");

        // Add new style based on logic
        switch (status) {
            case COMPLETED:
            case DELIVERED:
                lblStatusBadge.getStyleClass().add("status-done");
                break;
            case PENDING:
                lblStatusBadge.getStyleClass().add("status-warn");
                break;
            case CANCELLED:
                lblStatusBadge.getStyleClass().add("status-danger");
                break;
            default:
                lblStatusBadge.getStyleClass().add("status-pending");
        }

        // Update Progress Bar
        double max = RepairStatus.DELIVERED.ordinal();
        double current = status.ordinal();
        if(current > max) current = 0;
        progressWorkflow.setProgress(current / max);
    }

    private void setupListView() {
        listRepairJobs.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RepairJobTM item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Custom List Card
                    VBox vBox = new VBox(3);
                    Label name = new Label(item.customerNameProperty().get() + " - " + item.deviceNameProperty().get());
                    name.setStyle("-fx-font-weight: bold;");
                    Label status = new Label("Status: " + item.getStatus());
                    status.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
                    vBox.getChildren().addAll(name, status);
                    setGraphic(vBox);
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }

    private void loadMockData() {
        // Clear list to avoid duplicates on refresh
        masterData.clear();

        // MOCK DATA 1
        RepairJobDTO dto1 = new RepairJobDTO(1001, "Dell Latitude", "SN-555", "Hinge Broken",
                RepairStatus.PENDING, new Date(), null, 0,0,0, PaymentStatus.PENDING);
        masterData.add(new RepairJobTM(dto1, "Amal Perera", "077-1112222"));

        // MOCK DATA 2
        RepairJobDTO dto2 = new RepairJobDTO(1002, "iPhone X", "IMEI-999", "Battery Drain",
                RepairStatus.DIAGNOSIS, new Date(), null, 0,0,0, PaymentStatus.PENDING);
        masterData.add(new RepairJobTM(dto2, "Kamal Gunarathna", "071-3334444"));
    }
}