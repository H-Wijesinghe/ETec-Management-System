package lk.ijse.etecmanagementsystem.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.dto.tm.PendingRepairTM;
import lk.ijse.etecmanagementsystem.dto.tm.RepairPartTM;
import lk.ijse.etecmanagementsystem.util.CrudUtil;
import lk.ijse.etecmanagementsystem.util.ProductCondition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryDAOImpl {
    public ObservableList<PendingRepairTM> getPendingRepairs() throws SQLException {
        String repairSql = "SELECT r.repair_id, r.device_name, c.name, r.total_amount, r.paid_amount FROM RepairJob r JOIN Customer c ON r.cus_id = c.cus_id WHERE r.payment_status IN ('PENDING','PARTIAL') AND r.status IN ('DELIVERED')";


        ResultSet rs = CrudUtil.execute(repairSql);
        ObservableList<PendingRepairTM> pendingRepairsList = FXCollections.observableArrayList();

        while (rs.next()) {
            double balanceDue = rs.getDouble("total_amount") - rs.getDouble("paid_amount");


//            (int repairId, String device, String customerName, double balanceDue)
            pendingRepairsList.add(new PendingRepairTM(rs.getInt("repair_id"),
                    rs.getString("device_name"),
                    rs.getString("name"),
                    balanceDue));
        }
        rs.close();
        return pendingRepairsList;
    }

    public List<RepairPartTM> getUsedParts(int repairId) throws SQLException {
        List<RepairPartTM> list = new ArrayList<>();
//
        String sql = "SELECT pi.item_id, p.name, pi.serial_number, p.p_condition, ri.unit_price " +
                "FROM RepairItem ri " +
                "JOIN ProductItem pi ON ri.item_id = pi.item_id " +
                "JOIN Product p ON pi.stock_id = p.stock_id " +
                "WHERE ri.repair_id = ?";

        ResultSet rs = CrudUtil.execute(sql, repairId);
//        List<RepairJobDTO> usedPartsList = new ArrayList<>();

        while (rs.next()) {
            list.add(new RepairPartTM(
                    rs.getInt("item_id"),
                    rs.getString("name"),
                    rs.getString("serial_number"),
                    fromConditionString(rs.getString("p_condition")),
                    rs.getDouble("unit_price")
            ));
        }
        rs.close();
        return list;

    }

    private ProductCondition fromConditionString(String s) {
        if (s == null) return ProductCondition.BOTH;
        try {
            if (s.equals("USED")) {
                return ProductCondition.USED;
            } else if (s.equals("BRAND NEW")) {
                return ProductCondition.BRAND_NEW;
            }
            return ProductCondition.BOTH;
        } catch (IllegalArgumentException ex) {
            return ProductCondition.BOTH; // unknown condition value
        }
    }

}
