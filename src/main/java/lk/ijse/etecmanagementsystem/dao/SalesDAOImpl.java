package lk.ijse.etecmanagementsystem.dao;

import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.tm.SalesTM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesDAOImpl {
    public List<SalesTM> getAllSales() throws SQLException {
        List<SalesTM> salesList = new ArrayList<>();

        String sql = "SELECT s.sale_id, c.name AS customer_name, u.user_name, s.description, " +
                "s.sub_total, s.discount, s.grand_total, s.paid_amount " +
                "FROM Sales s " +
                "LEFT JOIN Customer c ON s.customer_id = c.cus_id " +
                "JOIN User u ON s.user_id = u.user_id " +
                "ORDER BY s.sale_date DESC";

        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                salesList.add(new SalesTM(
                        resultSet.getInt("sale_id"),
                        resultSet.getString("customer_name") != null ? resultSet.getString("customer_name") : "Walk-in", // Handle null customers
                        resultSet.getString("user_name"),
                        resultSet.getString("description"),
                        resultSet.getDouble("sub_total"),
                        resultSet.getDouble("discount"),
                        resultSet.getDouble("grand_total"),
                        resultSet.getDouble("paid_amount")
                ));
            }
        }
        return salesList;
    }

    public List<SalesTM> getSalesByDateRange(LocalDate from, LocalDate to) throws SQLException {
        List<SalesTM> salesList = new ArrayList<>();

        String sql = "SELECT s.sale_id, c.name AS customer_name, u.user_name, s.description, " +
                "s.sub_total, s.discount, s.grand_total, s.paid_amount " +
                "FROM Sales s " +
                "LEFT JOIN Customer c ON s.customer_id = c.cus_id " +
                "JOIN User u ON s.user_id = u.user_id " +
                "WHERE DATE(s.sale_date) BETWEEN ? AND ? " +
                "ORDER BY s.sale_date DESC";

        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setDate(1, java.sql.Date.valueOf(from));
            pstm.setDate(2, java.sql.Date.valueOf(to));

            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                salesList.add(new SalesTM(
                        resultSet.getInt("sale_id"),
                        resultSet.getString("customer_name") != null ? resultSet.getString("customer_name") : "Walk-in", // Handle null customers
                        resultSet.getString("user_name"),
                        resultSet.getString("description"),
                        resultSet.getDouble("sub_total"),
                        resultSet.getDouble("discount"),
                        resultSet.getDouble("grand_total"),
                        resultSet.getDouble("paid_amount")
                ));
            }
        }
        return salesList;
    }
}
