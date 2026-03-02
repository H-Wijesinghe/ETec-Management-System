package lk.ijse.etecmanagementsystem.dao.custom.impl;

import lk.ijse.etecmanagementsystem.dao.custom.ProductDAO;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.util.CrudUtil;
import lk.ijse.etecmanagementsystem.util.GenerateReports;
import lk.ijse.etecmanagementsystem.util.ProductCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    public boolean save(ProductDTO p) throws SQLException {
        String sql = "INSERT INTO Product (name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        return CrudUtil.execute(sql,
                p.getName(),
                p.getDescription(),
                p.getSellPrice(),
                p.getCategory(),
                p.getCondition().getLabel(),
                p.getBuyPrice(),
                p.getWarrantyMonth(),
                p.getQty(),
                p.getImagePath()
        );
    }

    public int getLastInsertedProductId() throws SQLException {
        String idQuery = "SELECT LAST_INSERT_ID() AS id FROM Product";
        ResultSet rs = CrudUtil.execute(idQuery);
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("Failed to retrieve product ID");
        }
    }

    public boolean update(ProductDTO p) throws SQLException {
        String sqlProduct = "UPDATE Product SET name=?, description=?, sell_price=?, category=?, p_condition=?, buy_price=?, warranty_months=?, qty=?, image_path=? WHERE stock_id=?";

        return CrudUtil.execute(sqlProduct,
                p.getName(),
                p.getDescription(),
                p.getSellPrice(),
                p.getCategory(),
                p.getCondition().getLabel(),
                p.getBuyPrice(),
                p.getWarrantyMonth(),
                p.getQty(),
                p.getImagePath(),
                p.getId()
        );
    }

    public boolean updateQty(int stockId, int value) throws SQLException {
        if(value < 0){
            int val = value * -1;
             String qtySql = "UPDATE Product SET qty = qty - ? WHERE stock_id = ?";
            return CrudUtil.execute(qtySql, val, stockId);
        }else {
            String qtySql = "UPDATE Product SET qty = qty + ? WHERE stock_id = ?";
            return CrudUtil.execute(qtySql, value, stockId);
        }

    }

    public boolean delete(int id) throws SQLException {
        String deleteProductSql = "DELETE FROM Product WHERE stock_id = ?";
        return CrudUtil.execute(deleteProductSql, id);
    }

    public ProductDTO findById(String id) throws SQLException {
        String sql = "SELECT stock_id, name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path FROM Product WHERE stock_id=?";

        ProductDTO product = null;
        ResultSet rs = CrudUtil.execute(sql, id);
        if (rs.next()) {
            product = new ProductDTO(
                    rs.getString("stock_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("sell_price"),
                    rs.getString("category"),
                    fromConditionString(rs.getString("p_condition")),
                    rs.getDouble("buy_price"),
                    rs.getInt("warranty_months"),
                    rs.getInt("qty"),
                    rs.getString("image_path")
            );
        }
        return product;
    }

    public int getIdByName(String name) throws SQLException {

        String sql = "SELECT stock_id FROM Product WHERE name=?";
        try (ResultSet rs = CrudUtil.execute(sql, name)) {
            if (rs.next()) {
                return rs.getInt("stock_id");
            } else {
                return -1;
            }
        }
    }

    public List<ProductDTO> getAll() throws SQLException {
//        String sql = "SELECT stock_id, name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path FROM Product ORDER BY name";
        String sql = "SELECT * FROM Product";
        List<ProductDTO> products = new ArrayList<>();

        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {

                products.add(new ProductDTO(
                        rs.getString("stock_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("sell_price"),
                        rs.getString("category"),
                        fromConditionString(rs.getString("p_condition")),
                        rs.getDouble("buy_price"),
                        rs.getInt("warranty_months"),
                        rs.getInt("qty"),
                        rs.getString("image_path")
                ));
            }
        }

        return products;
    }

    public int getInventoryCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Product ";
        return GenerateReports.getTotalCount(sql);
    }

    public int getLowStockCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Product WHERE qty < 5";
        int stock = 0;
        ResultSet rs = CrudUtil.execute(sql);
        if (rs.next()) stock = rs.getInt(1);
        rs.close();
        return stock;
    }

    private ProductCondition fromConditionString(String s) {
        if (s == null) return null;
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
