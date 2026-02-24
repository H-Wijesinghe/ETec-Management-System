package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    boolean save(ProductDTO p) throws SQLException;

    int getLastInsertedProductId() throws SQLException;

    boolean update(ProductDTO p) throws SQLException;

    boolean updateQty(int stockId, int value) throws SQLException;

    boolean delete(int id) throws SQLException;

    ProductDTO findById(String id) throws Exception;

    int getIdByName(String name) throws SQLException;

    List<ProductDTO> getAll() throws SQLException;
}

