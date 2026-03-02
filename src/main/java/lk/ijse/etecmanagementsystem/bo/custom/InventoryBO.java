package lk.ijse.etecmanagementsystem.bo.custom;

import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.dto.ProductItemDTO;
import lk.ijse.etecmanagementsystem.dto.SupplierDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public interface InventoryBO {
    int saveProductAndGetId(ProductDTO p) throws SQLException;

    boolean update(ProductDTO p) throws SQLException;

    boolean updateProductWithQtySync(ProductDTO p) throws SQLException;

    boolean deleteById(String stockId) throws SQLException;

    ItemDeleteStatus checkItemStatusForDelete(String stockId) throws SQLException;

    boolean addNewSerialNo(ArrayList<ProductItemDTO> itemDTOS) throws SQLException;

    Map<Integer, String> getAllProductMap() throws SQLException;

    Map<Integer, String> getAllSuppliersMap() throws SQLException;

    boolean correctItemMistake(String oldSerial, String newSerial, int newStockId, Integer newSupplierId, int newSupWar) throws SQLException;

    boolean updateItemStatus(String serial, String newStatus) throws SQLException;

    class ItemDeleteStatus {
        public final int realAvailableCount;
        public final int restrictedCount;

        public ItemDeleteStatus(int real, int restricted) {
            this.realAvailableCount = real;
            this.restrictedCount = restricted;
        }
    }
}

