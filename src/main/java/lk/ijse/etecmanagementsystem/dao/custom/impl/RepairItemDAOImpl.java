package lk.ijse.etecmanagementsystem.dao.custom.impl;

import lk.ijse.etecmanagementsystem.dao.custom.RepairItemDAO;
import lk.ijse.etecmanagementsystem.dto.RepairItemDTO;
import lk.ijse.etecmanagementsystem.dao.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RepairItemDAOImpl implements RepairItemDAO {

    public int getRepairItemId(int repairId, int itemId) throws SQLException {
        String sqlCheck = "SELECT id FROM RepairItem WHERE repair_id=? AND item_id=?";
        ResultSet rs =  CrudUtil.execute(sqlCheck, repairId, itemId);
        if (rs.next()) {
            return rs.getInt("id");
        }
        return -1; // Not found
    }

    public RepairItemDTO getRepairItemByRepairId(int repairId) throws SQLException {
        String sql = "SELECT * FROM RepairItem WHERE repair_id = ?";
        ResultSet rs = CrudUtil.execute(sql, repairId);
        if (rs.next()) {
            return new RepairItemDTO(
                    rs.getInt("id"),
                    rs.getInt("repair_id"),
                    rs.getInt("item_id"),
                    rs.getDouble("unit_price")
            );
        }
        return null; // Not found
    }

    public boolean saveRepairItem(int repairId, int itemId, double unitPrice) throws SQLException {
        String sqlInsertLink = "INSERT INTO RepairItem (repair_id, item_id, unit_price) VALUES (?, ?, ?)";
        return CrudUtil.execute(sqlInsertLink, repairId, itemId, unitPrice);

    }

    public boolean deleteRepairItem(int repairId, int itemId) throws SQLException {
        String sqlDeleteLink = "DELETE FROM RepairItem WHERE repair_id=? AND item_id=?";
        return CrudUtil.execute(sqlDeleteLink, repairId, itemId);
    }
}
