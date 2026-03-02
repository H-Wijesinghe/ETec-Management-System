package lk.ijse.etecmanagementsystem.bo.custom.impl;

import javafx.scene.chart.XYChart;
import lk.ijse.etecmanagementsystem.bo.custom.DashboardBO;
import lk.ijse.etecmanagementsystem.dao.custom.impl.*;
import lk.ijse.etecmanagementsystem.dto.CustomDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardBOImpl implements DashboardBO {

    RepairJobDAOImpl repairDAO = new RepairJobDAOImpl();
    SalesDAOImpl salesDAO = new SalesDAOImpl();
    QueryDAOImpl queryDAO = new QueryDAOImpl();
    TransactionRecordDAOImpl transactionDAO = new TransactionRecordDAOImpl();
    ProductDAOImpl productDAO = new ProductDAOImpl();

    @Override
    public CustomDTO getDashboardStats() throws SQLException {

        LocalDate today = LocalDate.now();

        double income = 0;
        int repairs = 0;
        int stock = 0;
        double debts = 0.0;

        // A. Today's Income (Only 'IN' transactions)
        income = transactionDAO.getTodayIncome(today);

        // B. Active Repairs (Not Completed/Delivered/Cancelled)
        repairs = repairDAO.getActiveRepairCount();

        // C. Low Stock (Items with qty < 5)
        stock = productDAO.getLowStockCount();

        // D. Total Pending Payments (Sales Due + Repair Due)
        debts = queryDAO.getDebts();

        return new CustomDTO(income, repairs, stock, debts);
    }

    @Override
    public List<CustomDTO> getUrgentRepairs() throws SQLException {
        return queryDAO.getUrgentRepairs();
    }

    @Override
    public List<CustomDTO> getUnpaidDebts() throws SQLException {
        return queryDAO.getUnpaidDebts();
    }

    @Override
    public XYChart.Series<String, Number> getSalesChartData() throws SQLException {
        return salesDAO.getSalesChartData();
    }

    @Override
    public List<XYChart.Series<String, Number>> getTrafficChartData() throws SQLException {
        List<XYChart.Series<String, Number>> allSeries = new ArrayList<>();

        XYChart.Series<String, Number> seriesSales = new XYChart.Series<>();
        seriesSales.setName("Sales");

        XYChart.Series<String, Number> seriesRepairs = new XYChart.Series<>();
        seriesRepairs.setName("Repairs");

        seriesSales = salesDAO.getSalesChartData();

        seriesRepairs = repairDAO.getRepairChartData();

        allSeries.add(seriesSales);
        allSeries.add(seriesRepairs);

        return allSeries;
    }
}
