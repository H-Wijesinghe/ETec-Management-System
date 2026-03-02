package lk.ijse.etecmanagementsystem.dao.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import lk.ijse.etecmanagementsystem.dao.custom.SalesDAO;
import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.SalesDTO;
import lk.ijse.etecmanagementsystem.dto.tm.PendingSaleTM;
import lk.ijse.etecmanagementsystem.dto.tm.SalesTM;
import lk.ijse.etecmanagementsystem.dao.CrudUtil;
import lk.ijse.etecmanagementsystem.util.GenerateReports;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class SalesDAOImpl implements SalesDAO {

    public List<SalesDTO> getAllSale() throws SQLException {
        List<SalesDTO> salesList = new ArrayList<>();

        String sql = "SELECT * FROM Sales s ORDER BY s.sale_date DESC";

        ResultSet resultSet = CrudUtil.execute(sql);
        while (resultSet.next()) {
            salesList.add(new SalesDTO(
                    resultSet.getInt("sale_id"),
                    resultSet.getInt("customer_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getTimestamp("sale_date"),
                    resultSet.getDouble("sub_total"),
                    resultSet.getDouble("discount"),
                    resultSet.getDouble("grand_total"),
                    resultSet.getDouble("paid_amount"),
                    resultSet.getString("payment_status"),
                    resultSet.getString("description")
            ));
        }
        resultSet.close();
        return salesList;
    }

    public SalesDTO getSaleById(int saleId) throws SQLException {
        String sql = "SELECT * FROM Sales WHERE sale_id = ?";
        ResultSet rs = CrudUtil.execute(sql, saleId);
        if (rs.next()) {
            return new SalesDTO(
                    rs.getInt("sale_id"),
                    rs.getInt("customer_id"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("sale_date"),
                    rs.getDouble("sub_total"),
                    rs.getDouble("discount"),
                    rs.getDouble("grand_total"),
                    rs.getDouble("paid_amount"),
                    rs.getString("payment_status"),
                    rs.getString("description")
            );

        }
        return null; // Sale not found
    }

    public boolean saveSale(SalesDTO salesDTO) throws SQLException {
        String sqlSales = "INSERT INTO Sales (customer_id, user_id, sale_date, sub_total, discount, " +
                "grand_total, paid_amount, payment_status, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return CrudUtil.execute(sqlSales,
                salesDTO.getCustomerId() == 0 ? null : salesDTO.getCustomerId(),
                salesDTO.getUserId(),
                new Timestamp(System.currentTimeMillis()),
                salesDTO.getSubtotal(),
                salesDTO.getDiscount(),
                salesDTO.getGrandTotal(),
                salesDTO.getPaidAmount(),
                salesDTO.getPaymentStatus().toString(),
                salesDTO.getDescription()
        );
    }

    public int getLastInsertedSalesId() throws SQLException {
        String idQuery = "SELECT LAST_INSERT_ID() AS id FROM Sales";
        ResultSet rs = CrudUtil.execute(idQuery);
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("Failed to retrieve sales ID");
        }
    }

    public boolean updateSalePayment(int saleId, double newPaidAmount, String newPaymentStatus) throws SQLException {
        String updateSql = "UPDATE Sales SET paid_amount = paid_amount + ?, payment_status = ? WHERE sale_id = ?";
        return CrudUtil.execute(updateSql, newPaidAmount, newPaymentStatus, saleId);
    }

    public int getSalesCount(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Sales WHERE sale_date BETWEEN ? AND ?";
        return GenerateReports.getCountByDateRange(sql, from, to);
    }

    public boolean isSaleExist(String saleId) throws SQLException {
        String sql = "SELECT sale_id FROM Sales WHERE sale_id = ?";
        return GenerateReports.checkIdExists(sql, saleId);
    }

    public XYChart.Series<String, Number> getSalesChartData() throws SQLException {
        String sqlSales = "SELECT DATE(sale_date) as d, COUNT(*) as c FROM Sales " +
                "WHERE sale_date >= DATE(NOW()) - INTERVAL 7 DAY " +
                "GROUP BY DATE(sale_date) ORDER BY DATE(sale_date)";
        XYChart.Series<String, Number> seriesSales = new XYChart.Series<>();
        seriesSales.setName("Sales");

        ResultSet rs1 = CrudUtil.execute(sqlSales);
        while (rs1.next()) {
            seriesSales.getData().add(new XYChart.Data<>(rs1.getString("d"), rs1.getInt("c")));
        }
        return seriesSales;
    }
}
