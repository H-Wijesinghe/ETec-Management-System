package lk.ijse.etecmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lk.ijse.etecmanagementsystem.dto.TopProductDTO;

import java.net.URL;
import java.sql.*;
        import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // --- FXML INJECTIONS (You must add these fx:id to your FXML file) ---

    // Top Stats Cards
    @FXML private Label lblTodaySales, lblTodaySalesGrowth;
    @FXML private Label lblTotalOrders, lblTotalOrdersGrowth;
    @FXML private Label lblPartsSold, lblPartsSoldGrowth; // Using 'ProductItem' count
    @FXML private Label lblNewClients, lblNewClientsGrowth;

    // Charts
    @FXML private LineChart<String, Number> chartVisitorInsights;
    @FXML private BarChart<String, Number> chartTotalRevenue;
    @FXML private BarChart<String, Number> chartTargetReality;
    @FXML private StackedBarChart<String, Number> chartVolumeService;

    // Top Products (Since your FXML uses a static Grid, we map the rows manually)
    @FXML private Label lblTop1Name, lblTop1Pct; @FXML private ProgressBar pbTop1;
    @FXML private Label lblTop2Name, lblTop2Pct; @FXML private ProgressBar pbTop2;
    @FXML private Label lblTop3Name, lblTop3Pct; @FXML private ProgressBar pbTop3;
    @FXML private Label lblTop4Name, lblTop4Pct; @FXML private ProgressBar pbTop4;

    // Database Connection String (Update with your DB credentials)
    private final String DB_URL = "jdbc:mysql://localhost:3306/ETec";
    private final String DB_USER = "root";
    private final String DB_PASS = "mysql";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSummaryCards();
        loadRevenueChart();
        loadTopProducts();
        loadServiceVsVolumeChart();
        // initializeVisitorChart(); // Mock data as DB doesn't track raw "visitors"
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // --- 1. LOAD SUMMARY CARDS ---
    private void loadSummaryCards() {
        try (Connection conn = getConnection()) {

            // A. Today's Sales (From TransactionRecord where type is payment)
            String sqlSales = "SELECT SUM(amount) FROM TransactionRecord " +
                    "WHERE transaction_type IN ('SALE_PAYMENT', 'REPAIR_PAYMENT') " +
                    "AND flow = 'IN' AND DATE(transaction_date) = CURDATE()";
            try (PreparedStatement ps = conn.prepareStatement(sqlSales); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double sales = rs.getDouble(1);
                    lblTodaySales.setText("$" + String.format("%.2f", sales));
                    // Logic for growth calculation would require querying yesterday's data similarly
                    lblTodaySalesGrowth.setText("+5% from yesterday"); // Placeholder logic
                }
            }

            // B. Total Orders (Count Sales from today)
            String sqlOrders = "SELECT COUNT(*) FROM Sales WHERE DATE(sale_date) = CURDATE()";
            try (PreparedStatement ps = conn.prepareStatement(sqlOrders); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblTotalOrders.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // C. Parts Sold (From SalesItem joined with Sales)
            String sqlParts = "SELECT SUM(si.qty) FROM SalesItem si " +
                    "JOIN Sales s ON si.sale_id = s.sale_id " +
                    "WHERE DATE(s.sale_date) = CURDATE()";
            try (PreparedStatement ps = conn.prepareStatement(sqlParts); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblPartsSold.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // D. New Clients (Count Customers added today - assuming we track date, or just count distinct Sales today)
            // Since Customer table doesn't have created_at, we count distinct customers in Sales today
            String sqlClients = "SELECT COUNT(DISTINCT customer_id) FROM Sales WHERE DATE(sale_date) = CURDATE()";
            try (PreparedStatement ps = conn.prepareStatement(sqlClients); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblNewClients.setText(String.valueOf(rs.getInt(1)));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- 2. LOAD REVENUE CHART ---
    private void loadRevenueChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        String sql = "SELECT DATE(transaction_date) as t_date, SUM(amount) as total " +
                "FROM TransactionRecord " +
                "WHERE flow = 'IN' " +
                "GROUP BY DATE(transaction_date) " +
                "ORDER BY t_date DESC LIMIT 7"; // Last 7 days

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // In a real app, reverse the list so it goes Mon->Sun
                series.getData().add(new XYChart.Data<>(rs.getString("t_date"), rs.getDouble("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        chartTotalRevenue.getData().add(series);
    }

    // --- 3. LOAD TOP PRODUCTS ---
    private void loadTopProducts() {
        // Query: Get Product Name and Sum of Qty sold, ordered by highest Sum
        String sql = "SELECT p.name, SUM(si.qty) as total_sold " +
                "FROM SalesItem si " +
                "JOIN ProductItem pi ON si.item_id = pi.item_id " +
                "JOIN Product p ON pi.stock_id = p.stock_id " +
                "GROUP BY p.stock_id " +
                "ORDER BY total_sold DESC LIMIT 4";

        List<TopProductDTO> products = new ArrayList<>();
        double maxSold = 1.0; // Avoid divide by zero

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                int sold = rs.getInt("total_sold");
                if (rank == 1) maxSold = sold; // Assume first is max

                products.add(new TopProductDTO(rank++, name, (double)sold/maxSold, sold + " sold"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bind data to UI (Manual binding because FXML is static Grid)
        if (products.size() > 0) updateProductRow(products.get(0), lblTop1Name, pbTop1, lblTop1Pct);
        if (products.size() > 1) updateProductRow(products.get(1), lblTop2Name, pbTop2, lblTop2Pct);
        if (products.size() > 2) updateProductRow(products.get(2), lblTop3Name, pbTop3, lblTop3Pct);
        if (products.size() > 3) updateProductRow(products.get(3), lblTop4Name, pbTop4, lblTop4Pct);
    }

    private void updateProductRow(TopProductDTO p, Label name, ProgressBar pb, Label pct) {
        name.setText(p.getName());
        pb.setProgress(p.getPopularity());
        pct.setText(p.getSalesPercentageText());
    }

    // --- 4. LOAD VOLUME VS SERVICES (Stacked Bar) ---
    private void loadServiceVsVolumeChart() {
        XYChart.Series<String, Number> seriesSales = new XYChart.Series<>();
        seriesSales.setName("Product Sales");

        XYChart.Series<String, Number> seriesRepairs = new XYChart.Series<>();
        seriesRepairs.setName("Repairs");

        // Mocking logic for example (You would query GROUP BY MONTH)
        seriesSales.getData().add(new XYChart.Data<>("Jan", 1135));
        seriesRepairs.getData().add(new XYChart.Data<>("Jan", 635));

        seriesSales.getData().add(new XYChart.Data<>("Feb", 1400));
        seriesRepairs.getData().add(new XYChart.Data<>("Feb", 500));

        chartVolumeService.getData().addAll(seriesSales, seriesRepairs);
    }
}