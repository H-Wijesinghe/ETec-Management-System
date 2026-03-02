package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dao.CrudDAO;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.entity.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO extends CrudDAO<Product> {

    int getLastInsertedProductId() throws SQLException;

    boolean updateQty(int stockId, int value) throws SQLException;

    int getIdByName(String name) throws SQLException;

    int getInventoryCount() throws SQLException;

    int getLowStockCount() throws SQLException;
}

