package lk.ijse.etecmanagementsystem.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.tm.PendingRepairTM;
import lk.ijse.etecmanagementsystem.dto.tm.PendingSaleTM;
import lk.ijse.etecmanagementsystem.dto.tm.TransactionTM;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class TransactionsController {

    // --- FXML UI Components ---
    @FXML
    private Label lblTotalIncome, lblTotalExpense, lblNetProfit;
    @FXML
    private DatePicker dpFromDate, dpToDate;
    @FXML
    private ComboBox<String> comboTypeFilter;
    @FXML
    private TextField txtSearchHistory;

    // Table: History
    @FXML
    private TableView<TransactionTM> tblHistory;
    @FXML
    private TableColumn<TransactionTM, Integer> colHistId;
    @FXML
    private TableColumn<TransactionTM, String> colHistDate, colHistType, colHistRef, colHistFlow, colHistUser;
    @FXML
    private TableColumn<TransactionTM, Double> colHistAmount;

    // Table: Pending Sales
    @FXML
    private TableView<PendingSaleTM> tblPendingSales;
    @FXML
    private TableColumn<PendingSaleTM, Integer> colSaleId;
    @FXML
    private TableColumn<PendingSaleTM, String> colSaleCustomer;
    @FXML
    private TableColumn<PendingSaleTM, Double> colSaleTotal, colSaleDue;
    @FXML
    private TableColumn<PendingSaleTM, Void> colSaleAction;

    // Table: Pending Repairs
    @FXML
    private TableView<PendingRepairTM> tblPendingRepairs;
    @FXML
    private TableColumn<PendingRepairTM, Integer> colRepairId;
    @FXML
    private TableColumn<PendingRepairTM, String> colRepairDevice, colRepairCustomer;
    @FXML
    private TableColumn<PendingRepairTM, Double> colRepairDue;
    @FXML
    private TableColumn<PendingRepairTM, Void> colRepairAction;

    // --- Initialization ---
    public void initialize() {
        setupTables();
        setupFilters();
        loadDashboardData(); // Load "Today's" stats by default
        loadHistory();
        loadPendingSettlements();
    }

    private void setupTables() {
        // Map History Columns
        colHistId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colHistDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colHistType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colHistRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colHistFlow.setCellValueFactory(new PropertyValueFactory<>("flow"));
        colHistAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colHistUser.setCellValueFactory(new PropertyValueFactory<>("user"));

        // Map Pending Sales Columns
        colSaleId.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        colSaleCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colSaleTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colSaleDue.setCellValueFactory(new PropertyValueFactory<>("balanceDue"));
        addSettleButtonToSales(); // Add the "PAY" button

        // Map Pending Repairs Columns
        colRepairId.setCellValueFactory(new PropertyValueFactory<>("repairId"));
        colRepairDevice.setCellValueFactory(new PropertyValueFactory<>("device"));
        colRepairCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colRepairDue.setCellValueFactory(new PropertyValueFactory<>("balanceDue"));
        addSettleButtonToRepairs(); // Add the "PAY" button
    }

    private void setupFilters() {
        dpFromDate.setValue(LocalDate.now());
        dpToDate.setValue(LocalDate.now());
        comboTypeFilter.setItems(FXCollections.observableArrayList("All", "SALE_PAYMENT", "REPAIR_PAYMENT", "EXPENSE", "SUPPLIER_PAYMENT"));
        comboTypeFilter.getSelectionModel().selectFirst();
    }

    // --- Data Loading Methods ---

    @FXML
    public void loadHistory() {
        ObservableList<TransactionTM> list = FXCollections.observableArrayList();
        String sql = "SELECT t.*, u.user_name FROM TransactionRecord t JOIN User u ON t.user_id = u.user_id WHERE DATE(t.transaction_date) BETWEEN ? AND ?";
        // Add type filtering logic here if needed
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(dpFromDate.getValue()));
                stmt.setDate(2, Date.valueOf(dpToDate.getValue()));
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    list.add(new TransactionTM(
                            rs.getInt("transaction_id"),
                            rs.getString("transaction_date"),
                            rs.getString("transaction_type"),
                            rs.getString("reference_note"),
                            rs.getString("flow"),
                            rs.getDouble("amount"),
                            rs.getString("user_name")
                    ));
                }
                rs.close();
                tblHistory.setItems(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPendingSettlements() {
        // 1. Load Sales
        ObservableList<PendingSaleTM> salesList = FXCollections.observableArrayList();
        String saleSql = "SELECT s.sale_id, c.name, s.grand_total, s.paid_amount FROM Sales s JOIN Customer c ON s.customer_id = c.cus_id WHERE s.payment_status IN ('PENDING', 'PARTIAL')";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(saleSql)) {
                while (rs.next()) {
                    double total = rs.getDouble("grand_total");
                    double paid = rs.getDouble("paid_amount");
                    salesList.add(new PendingSaleTM(rs.getInt("sale_id"), rs.getString("name"), total, total - paid));
                }
                tblPendingSales.setItems(salesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Load Repairs (Similar logic for Repairs table)
        // ... (Code for loading repairs using the Query B provided above) ...
    }

    @FXML
    private void handleManualTransaction() {
        // 1. Create the Dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Transaction");
        dialog.setHeaderText("Record an Expense or Manual Payment");

        // 2. Create the Form Elements
        Label lblType = new Label("Type:");
        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.setItems(FXCollections.observableArrayList("EXPENSE", "SUPPLIER_PAYMENT", "OTHER_INCOME"));
        cmbType.getSelectionModel().select("EXPENSE");

        Label lblAmount = new Label("Amount:");
        TextField txtAmount = new TextField();
        txtAmount.setPromptText("0.00");

        Label lblMethod = new Label("Method:");
        ComboBox<String> cmbMethod = new ComboBox<>();
        cmbMethod.setItems(FXCollections.observableArrayList("CASH", "CARD", "TRANSFER"));
        cmbMethod.getSelectionModel().select("CASH");

        Label lblNote = new Label("Note/Ref:");
        TextField txtNote = new TextField();
        txtNote.setPromptText("e.g. Electricity Bill, Tea, Stock Purchase");

        // 3. Layout the Form (GridPane)
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(lblType, 0, 0);
        grid.add(cmbType, 1, 0);
        grid.add(lblAmount, 0, 1);
        grid.add(txtAmount, 1, 1);
        grid.add(lblMethod, 0, 2);
        grid.add(cmbMethod, 1, 2);
        grid.add(lblNote, 0, 3);
        grid.add(txtNote, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // 4. Add Buttons
        ButtonType saveButtonType = new ButtonType("Save Record", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 5. Validation (Disable Save if amount is empty)
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        // 6. Handle Save Action
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return saveButtonType; // Just return the button type to trigger the logic
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButtonType) {
            saveManualTransaction(
                    cmbType.getValue(),
                    Double.parseDouble(txtAmount.getText()),
                    cmbMethod.getValue(),
                    txtNote.getText()
            );
        }
    }

    private void saveManualTransaction(String type, double amount, String method, String note) {
        String sql = "INSERT INTO TransactionRecord (transaction_type, payment_method, amount, flow, user_id, reference_note) VALUES (?, ?, ?, ?, ?, ?)";

        // Determine Flow: EXPENSE and SUPPLIER_PAYMENT are 'OUT', OTHER_INCOME is 'IN'
        String flow = (type.equals("EXPENSE") || type.equals("SUPPLIER_PAYMENT")) ? "OUT" : "IN";

        // Retrieve current User ID (Hardcoded to 1 for now, replace with Session.userId)
        int currentUserId = 1;
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, type);
                stmt.setString(2, method);
                stmt.setDouble(3, amount);
                stmt.setString(4, flow);
                stmt.setInt(5, currentUserId);
                stmt.setString(6, note);

                if(stmt.executeUpdate() <= 0){
                    new Alert(Alert.AlertType.ERROR, "Failed to save transaction.").show();
                    return;
                }

                // Success Message
                new Alert(Alert.AlertType.INFORMATION, "Transaction Saved Successfully!").show();

                // Refresh the Dashboard and List
                loadDashboardData();
                loadHistory();

            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    private void loadDashboardData() {
        // We will load stats for "Today" by default
        LocalDate today = LocalDate.now();
        String sql = "SELECT " +
                "SUM(CASE WHEN flow = 'IN' THEN amount ELSE 0 END) as total_in, " +
                "SUM(CASE WHEN flow = 'OUT' THEN amount ELSE 0 END) as total_out " +
                "FROM TransactionRecord " +
                "WHERE DATE(transaction_date) = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setDate(1, Date.valueOf(today));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    double in = rs.getDouble("total_in");
                    double out = rs.getDouble("total_out");
                    double net = in - out;

                    lblTotalIncome.setText(String.format("%.2f", in));
                    lblTotalExpense.setText(String.format("%.2f", out));
                    lblNetProfit.setText(String.format("%.2f", net));

                    // Optional: Change color of Net Profit if negative
                    if (net < 0) {
                        lblNetProfit.setStyle("-fx-text-fill: red;");
                    }
                } else {
                    lblNetProfit.setStyle("-fx-text-fill: #2980b9;"); // Blue
                }
                rs.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- The "Settle Balance" Logic ---

    private void addSettleButtonToSales() {
        colSaleAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Settle");

            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    PendingSaleTM sale = getTableView().getItems().get(getIndex());
                    showPaymentDialog("SALE", sale.getSaleId(), sale.getBalanceDue());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void addSettleButtonToRepairs() {
        colRepairAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Settle");

            {
                btn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    PendingRepairTM repair = getTableView().getItems().get(getIndex());
                    showPaymentDialog("REPAIR", repair.getRepairId(), repair.getBalanceDue());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    // --- The Popup Dialog for Payment ---
    private void showPaymentDialog(String type, int id, double dueAmount) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(dueAmount));
        dialog.setTitle("Settle Balance");
        dialog.setHeaderText("Enter Payment Amount for " + type + " #" + id);
        dialog.setContentText("Amount:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > dueAmount) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Amount cannot exceed balance due!");
                    alert.show();
                    return;
                }
                processPayment(type, id, amount);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid Amount").show();
            }
        });
    }

    // --- The Transaction Execution ---
    private void processPayment(String type, int id, double amount) {
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert Record into TransactionRecord
            String insertTrans = "INSERT INTO TransactionRecord (transaction_type, payment_method, amount, flow, " +
                    (type.equals("SALE") ? "sale_id" : "repair_id") + ", user_id, reference_note) VALUES (?, 'CASH', ?, 'IN', ?, 1, 'Partial Settlement')";
            // NOTE: Replace '1' with current logged-in user ID
            try (PreparedStatement ps1 = conn.prepareStatement(insertTrans)) {
                ps1.setString(1, type.equals("SALE") ? "SALE_PAYMENT" : "REPAIR_PAYMENT");
                ps1.setDouble(2, amount);
                ps1.setInt(3, id);
                if (ps1.executeUpdate() <= 0) {
                    throw new SQLException("Failed to record transaction.");
                }
            }


            // 2. Update the Source Table (Sales or RepairJob)
            String updateSql;
            if (type.equals("SALE")) {
                updateSql = "UPDATE Sales SET paid_amount = paid_amount + ?, payment_status = CASE WHEN (paid_amount + ?) >= grand_total THEN 'PAID' ELSE 'PARTIAL' END WHERE sale_id = ?";
            } else {
                updateSql = "UPDATE RepairJob SET paid_amount = paid_amount + ?, payment_status = CASE WHEN (paid_amount + ?) >= total_amount THEN 'PAID' ELSE 'PARTIAL' END WHERE repair_id = ?";
            }
            try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                ps2.setDouble(1, amount);
                ps2.setDouble(2, amount); // Check value
                ps2.setInt(3, id);
                if (ps2.executeUpdate() <= 0) {
                    throw new SQLException("Failed to update payment status.");
                }
            }

            conn.commit(); // Commit Transaction

            // Refresh UI
            loadPendingSettlements();
            loadHistory();
            loadDashboardData();

            new Alert(Alert.AlertType.INFORMATION, "Payment Successful!").show();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);

            } catch (SQLException e) {
            }
        }
    }
}