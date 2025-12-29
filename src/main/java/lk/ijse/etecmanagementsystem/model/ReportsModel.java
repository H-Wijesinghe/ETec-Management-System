package lk.ijse.etecmanagementsystem.model;

import lk.ijse.etecmanagementsystem.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportsModel {

    // ----------------- SALES -----------------
    public int getSalesCount(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Sales WHERE sale_date BETWEEN ? AND ?";
        return getCountByDateRange(sql, from, to);
    }

    public boolean isSaleExist(String saleId) throws SQLException {
        String sql = "SELECT sale_id FROM Sales WHERE sale_id = ?";
        return checkIdExists(sql, saleId);
    }

    // ----------------- REPAIR -----------------
    public int getRepairCount(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RepairJob WHERE date_in BETWEEN ? AND ?";
        return getCountByDateRange(sql, from, to);
    }

    public boolean isRepairExist(String repairId) throws SQLException {
        String sql = "SELECT repair_id FROM RepairJob WHERE repair_id = ?";
        return checkIdExists(sql, repairId);
    }

    // ----------------- TRANSACTION -----------------
    public int getTransactionCount(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TransactionRecord WHERE transaction_date BETWEEN ? AND ?";
        return getCountByDateRange(sql, from, to);
    }

    // ----------------- INVENTORY -----------------
    public int getInventoryCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ProductItem WHERE status = 'AVAILABLE'";
        return getTotalCount(sql);
    }

    // ----------------- SUPPLIER -----------------
    public int getSupplierCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Supplier";
        return getTotalCount(sql);
    }

    // ----------------- CUSTOMER -----------------
    public int getCustomerCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customer";
        return getTotalCount(sql);
    }

    // ----------------- HELPER METHODS -----------------
    private int getCountByDateRange(String sql, LocalDate from, LocalDate to) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        // Convert LocalDate to SQL Date, ensuring the end date includes the full day
//        pstm.setDate(1, java.sql.Date.valueOf(from));
//        pstm.setDate(2, java.sql.Date.valueOf(to));

        // Start of the 'From' day (00:00:00)
        pstm.setString(1, from.toString() + " 00:00:00");

        // End of the 'To' day (23:59:59)
        pstm.setString(2, to.toString() + " 23:59:59");

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    private int getTotalCount(String sql) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    private boolean checkIdExists(String sql, String id) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);
        ResultSet resultSet = pstm.executeQuery();
        return resultSet.next();
    }
}