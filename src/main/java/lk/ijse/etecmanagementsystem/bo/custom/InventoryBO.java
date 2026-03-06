package lk.ijse.etecmanagementsystem.bo.custom;

import lk.ijse.etecmanagementsystem.bo.SuperBO;
import lk.ijse.etecmanagementsystem.bo.custom.impl.InventoryBOImpl;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.dto.ProductItemDTO;
import lk.ijse.etecmanagementsystem.dto.SupplierDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface InventoryBO extends SuperBO {
    int saveProductAndGetId(ProductDTO p) throws SQLException;

    List<ProductDTO> getAllProduct() throws SQLException;

    boolean updateProductWithQtySync(ProductDTO p) throws SQLException;

    boolean deleteById(String stockId) throws SQLException;

    ProductDTO findById(String id) throws SQLException;

    InventoryBOImpl.ItemDeleteStatus checkItemStatusForDelete(String stockId) throws SQLException;

    boolean addNewSerialNo(ArrayList<ProductItemDTO> itemDTOS) throws SQLException;

    Map<Integer, String> getAllProductMap() throws SQLException;

    Map<Integer, String> getAllSuppliersMap() throws SQLException;

    boolean correctItemMistake(String oldSerial, String newSerial, int newStockId, Integer newSupplierId, int newSupWar) throws SQLException;

    boolean updateItemStatus(String serial, String newStatus) throws SQLException;

    int getIdByName(String name) throws SQLException;

    ArrayList<ProductItemDTO> getPlaceHolderItems(int stockId) throws SQLException;

    List<ProductItemDTO> getAllAvailableItems() throws SQLException;

    boolean checkSerialExists(String serial) throws SQLException;

    List<ProductItemDTO> getAllProductItems() throws SQLException;

    boolean updateCustomerWarranty(int customerWarranty, int stockId) throws SQLException;

    int getRealItemCount(int stockId) throws SQLException;

    boolean delete(int stockId) throws SQLException;

    int getRestrictedRealItemCount(int stockId) throws SQLException;
}

