package lk.ijse.etecmanagementsystem.dao.custom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.etecmanagementsystem.bo.SuperBO;
import lk.ijse.etecmanagementsystem.dao.CrudUtil;
import lk.ijse.etecmanagementsystem.dto.CustomDTO;
import lk.ijse.etecmanagementsystem.dto.tm.PendingRepairTM;
import lk.ijse.etecmanagementsystem.dto.tm.RepairPartTM;
import lk.ijse.etecmanagementsystem.util.ProductCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface QueryDAO extends SuperBO {
    List<CustomDTO> getPendingRepairs() throws SQLException;

    List<CustomDTO> getUsedParts(int repairId) throws SQLException;

    List<CustomDTO> getUrgentRepairs() throws SQLException;

    List<CustomDTO> getUnpaidDebts() throws SQLException;

    double getDebts() throws SQLException;

    List<CustomDTO> getAllProductItems() throws SQLException;

    List<CustomDTO> getAllAvailableRealItems() throws SQLException;

    CustomDTO getProductItem(int itemId) throws SQLException;

    List<CustomDTO> getUnitsByStockId(int stockId, String productName) throws SQLException;

    CustomDTO getItemBySerial(String serial) throws SQLException;

    List<CustomDTO> getSalesByDateRange(LocalDate from, LocalDate to) throws SQLException;

    List<CustomDTO> getPendingSales() throws SQLException;
}

