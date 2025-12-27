package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lk.ijse.etecmanagementsystem.db.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class DashboardController {

    // --- FXML Injections ---
    // Top Dashboard Cards
    @FXML
    private Label lblTodayIncome;
    @FXML
    private Label lblActiveRepairs;
    @FXML
    private Label lblPendingPayment;
    @FXML
    private Label lblLowStock;

    // The Beautiful Lists (Replaces the ugly TableViews)
    @FXML
    private ListView<UrgentRepairModel> listUrgentRepairs;
    @FXML
    private ListView<DebtModel> listUnpaid;

    // Bottom Chart
    @FXML
    private BarChart<String, Number> chartSales;

    // --- Initialization ---
    public void initialize() {
        // 1. Load the Numbers (Top Cards)
        loadDashboardStats();

        // 2. Configure the "Card" Look for the Lists
        setupRepairListDesign();
        setupDebtListDesign();

        // 3. Load Data into Lists
        loadUrgentRepairs();
        loadUnpaidDebts();

        // 4. Load the Graph
        loadSalesChart();
    }

    // =================================================================================
    // SECTION 1: VISUAL DESIGN (CELL FACTORIES)
    // =================================================================================

    /**
     * Renders each Urgent Repair as a clean row with an orange ID badge.
     */
    private void setupRepairListDesign() {
        listUrgentRepairs.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UrgentRepairModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;"); // Keep empty cells clear
                } else {
                    // Main Container
                    HBox hBox = new HBox(15);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setStyle("-fx-padding: 5 0 5 0;");

                    // 1. The Badge (Orange Circle with ID)
                    StackPane badge = new StackPane();
                    Circle circle = new Circle(15, Color.web("#e67e22"));
                    Label idLbl = new Label(String.valueOf(item.getId()));
                    idLbl.setTextFill(Color.WHITE);
                    idLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
                    badge.getChildren().addAll(circle, idLbl);

                    // 2. Middle Text (Device Name & Status)
                    VBox vBox = new VBox(3);
                    Label deviceLbl = new Label(item.getDevice());
                    deviceLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
                    deviceLbl.setTextFill(Color.web("#2c3e50"));

                    Label statusLbl = new Label(item.getStatus());
                    statusLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
                    vBox.getChildren().addAll(deviceLbl, statusLbl);

                    // 3. Spacer ( pushes Date to right)
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // 4. Right Side (Date)
                    Label dateLbl = new Label(item.getDate());
                    dateLbl.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");

                    hBox.getChildren().addAll(badge, vBox, spacer, dateLbl);
                    setGraphic(hBox);
                }
            }
        });
    }

    /**
     * Renders each Debt as a row with the Customer Name and Red Amount.
     */
    private void setupDebtListDesign() {
        listUnpaid.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DebtModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox hBox = new HBox(15);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setStyle("-fx-padding: 5 0 5 0;");

                    // 1. Icon (S for Sale, R for Repair)
                    Label icon = new Label(item.getType().substring(0, 1));
                    String iconColor = item.getType().equals("SALE") ? "#3498db" : "#9b59b6";
                    icon.setStyle("-fx-background-color: " + iconColor + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-weight: bold;");

                    // 2. Customer Name
                    VBox vBox = new VBox(3);
                    Label nameLbl = new Label(item.getCustomer());
                    nameLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
                    nameLbl.setTextFill(Color.web("#2c3e50"));

                    Label typeLbl = new Label(item.getType() + " Payment");
                    typeLbl.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 10px;");
                    vBox.getChildren().addAll(nameLbl, typeLbl);

                    // 3. Spacer
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // 4. Amount Due (Bold Red)
                    Label amountLbl = new Label(String.format("%.2f", item.getAmount()));
                    amountLbl.setTextFill(Color.web("#c0392b"));
                    amountLbl.setFont(Font.font("System", FontWeight.BOLD, 14));

                    hBox.getChildren().addAll(icon, vBox, spacer, amountLbl);
                    setGraphic(hBox);
                }
            }
        });
    }

    // =================================================================================
    // SECTION 2: DATA LOADING (DATABASE)
    // =================================================================================

    private void loadDashboardStats() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            LocalDate today = LocalDate.now();

            // A. Today's Income (Sum of IN transactions)
            String sqlIncome = "SELECT SUM(amount) FROM TransactionRecord WHERE flow='IN' AND DATE(transaction_date) = '" + today + "'";
            ResultSet rs1 = stmt.executeQuery(sqlIncome);
            if (rs1.next()) {
                double val = rs1.getDouble(1);
                lblTodayIncome.setText(String.format("%.2f", val));
            }

            // B. Active Repairs (Anything NOT Completed/Delivered/Cancelled)
            String sqlActiveRep = "SELECT COUNT(*) FROM RepairJob WHERE status NOT IN ('COMPLETED', 'DELIVERED', 'CANCELLED')";
            ResultSet rs2 = stmt.executeQuery(sqlActiveRep);
            if (rs2.next()) {
                lblActiveRepairs.setText(String.valueOf(rs2.getInt(1)));
            }

            // C. Low Stock (Alert if Qty < 5)
            String sqlStock = "SELECT COUNT(*) FROM Product WHERE qty < 5";
            ResultSet rs3 = stmt.executeQuery(sqlStock);
            if (rs3.next()) {
                lblLowStock.setText(String.valueOf(rs3.getInt(1)));
            }

            // D. Total Pending Payments (Sales Balance + Repair Balance)
            String sqlDebts = "SELECT " +
                    "(SELECT COALESCE(SUM(grand_total - paid_amount),0) FROM Sales WHERE payment_status != 'PAID') + " +
                    "(SELECT COALESCE(SUM(total_amount - paid_amount),0) FROM RepairJob WHERE payment_status != 'PAID' AND status != 'CANCELLED')";
            ResultSet rs4 = stmt.executeQuery(sqlDebts);
            if (rs4.next()) {
                lblPendingPayment.setText(String.format("%.2f", rs4.getDouble(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadUrgentRepairs() {
        ObservableList<UrgentRepairModel> list = FXCollections.observableArrayList();
        // Fetch oldest pending jobs first
        String sql = "SELECT repair_id, device_name, status, DATE(date_in) as d_in FROM RepairJob " +
                "WHERE status IN ('PENDING', 'DIAGNOSIS', 'WAITING_PARTS') " +
                "ORDER BY date_in ASC LIMIT 15";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new UrgentRepairModel(
                            rs.getInt("repair_id"),
                            rs.getString("device_name"),
                            rs.getString("status"),
                            rs.getString("d_in")
                    ));
                }
                listUrgentRepairs.setItems(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUnpaidDebts() {
        ObservableList<DebtModel> list = FXCollections.observableArrayList();
        // Combine Sales Debts AND Repair Debts into one list
        String sql = "SELECT 'SALE' as type, c.name, (s.grand_total - s.paid_amount) as due FROM Sales s JOIN Customer c ON s.customer_id = c.cus_id WHERE s.payment_status != 'PAID' " +
                "UNION ALL " +
                "SELECT 'REPAIR' as type, c.name, (r.total_amount - r.paid_amount) as due FROM RepairJob r JOIN Customer c ON r.cus_id = c.cus_id WHERE r.payment_status != 'PAID' AND r.status != 'CANCELLED'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new DebtModel(
                            rs.getString("type"),
                            rs.getString("name"),
                            rs.getDouble("due")
                    ));
                }
                listUnpaid.setItems(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSalesChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        // Get Last 7 Days revenue
        String sql = "SELECT DATE(transaction_date) as d, SUM(amount) as total FROM TransactionRecord " +
                "WHERE flow='IN' AND transaction_date >= DATE(NOW()) - INTERVAL 7 DAY " +
                "GROUP BY DATE(transaction_date) ORDER BY DATE(transaction_date)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
                while (rs.next()) {
                    series.getData().add(new XYChart.Data<>(rs.getString("d"), rs.getDouble("total")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        chartSales.getData().clear();
        chartSales.getData().add(series);
    }

    // =================================================================================
    // SECTION 3: INNER DATA MODELS
    // =================================================================================

    public static class UrgentRepairModel {
        private final int id;
        private final String device;
        private final String status;
        private final String date;

        public UrgentRepairModel(int id, String device, String status, String date) {
            this.id = id;
            this.device = device;
            this.status = status;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public String getDevice() {
            return device;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }
    }

    public static class DebtModel {
        private final String type;
        private final String customer;
        private final double amount;

        public DebtModel(String type, String customer, double amount) {
            this.type = type;
            this.customer = customer;
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public String getCustomer() {
            return customer;
        }

        public double getAmount() {
            return amount;
        }
    }
}