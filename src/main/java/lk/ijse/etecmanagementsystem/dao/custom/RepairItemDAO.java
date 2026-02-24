package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dto.RepairItemDTO;

import java.sql.SQLException;

public interface RepairItemDAO {
    int getRepairItemId(int repairId, int itemId) throws SQLException;

    RepairItemDTO getRepairItemByRepairId(int repairId) throws SQLException;

    boolean saveRepairItem(int repairId, int itemId, double unitPrice) throws SQLException;

    boolean deleteRepairItem(int repairId, int itemId) throws SQLException;
}

