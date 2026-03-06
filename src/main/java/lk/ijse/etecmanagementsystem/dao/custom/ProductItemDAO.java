package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dao.CrudUtil;
import lk.ijse.etecmanagementsystem.dto.InventoryItemDTO;
import lk.ijse.etecmanagementsystem.dto.ProductItemDTO;
import lk.ijse.etecmanagementsystem.entity.ProductItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ProductItemDAO {

    List<ProductItem> getAllAvailableItems() throws SQLException;

    boolean addPlaceHolderItem(int stockId, int limit) throws SQLException;

    boolean addProductItem(ProductItem entity) throws SQLException;

    ArrayList<ProductItem> getPlaceHolderItems(int stockId) throws SQLException;

    boolean updateItem(ProductItem entity) throws SQLException;

    boolean updateSerialNumber(int itemId, String serialNumber) throws SQLException;

    boolean updateStatus(String serialNumber, String status) throws SQLException;

    boolean updateItemAvailability(int itemId) throws SQLException;

    boolean updateCustomerWarranty(int customerWarranty, int stockId) throws SQLException;

    boolean updateItemForSale(ProductItemDTO item) throws SQLException;

    boolean updateItemForRepair(int itemId) throws SQLException;

    boolean fixSerialForRepair(int itemId) throws SQLException;

    boolean replaceSerialForReturned(int itemId) throws SQLException;

    int getRealItemCount(int stockId) throws SQLException;

    int getAvailableItemCount(int stockId) throws SQLException;

    boolean deletePlaceHolderItems(int stockId, int removeCount) throws SQLException;

    boolean delete(int stockId) throws SQLException;

    boolean checkSerialExists(String serial) throws SQLException;

    int getRestrictedRealItemCount(int stockId) throws SQLException;
}

