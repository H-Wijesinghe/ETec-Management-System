package lk.ijse.etecmanagementsystem.model;


import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.util.CrudUtil;
import lk.ijse.etecmanagementsystem.util.ProductCondition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProductModel {
    public boolean save(ProductDTO p) throws Exception {
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

    public boolean update(ProductDTO p) throws SQLException {
        Connection connection = null;

        // 1. Update Product Table (Standard info)
        String sqlProduct = "UPDATE Product SET name=?, description=?, sell_price=?, category=?, p_condition=?, buy_price=?, warranty_months=?, qty=?, image_path=? WHERE stock_id=?";

        // 2. Update ProductItem Table (Sync Warranty for unsold items only)
        String sqlItem = "UPDATE ProductItem SET customer_warranty_mo = ? WHERE stock_id = ? AND status = 'AVAILABLE'";

        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Start Transaction

            // --- STEP 1: Update the Main Product ---
            try (PreparedStatement pstm = connection.prepareStatement(sqlProduct)) {
                pstm.setString(1, p.getName());
                pstm.setString(2, p.getDescription());
                pstm.setDouble(3, p.getSellPrice());
                pstm.setString(4, p.getCategory());
                pstm.setString(5, p.getCondition().getLabel());
                pstm.setDouble(6, p.getBuyPrice());
                pstm.setInt(7, p.getWarrantyMonth());
                pstm.setInt(8, p.getQty());
                pstm.setString(9, p.getImagePath());
                pstm.setString(10, p.getId());

                int rows = pstm.executeUpdate();
                if (rows == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // --- STEP 2: Sync items on the shelf ---
            try (PreparedStatement pstmItem = connection.prepareStatement(sqlItem)) {
                // Set new warranty period
                pstmItem.setInt(1, p.getWarrantyMonth());
                // Only for this product ID
                pstmItem.setInt(2, Integer.parseInt(p.getId()));

                pstmItem.executeUpdate();
            }

            connection.commit(); // Save both changes
            return true;

        } catch (SQLException e) {
            if (connection != null) connection.rollback(); // Undo if error
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    public boolean deleteById(String id) throws Exception {
        String sql = "DELETE FROM Product WHERE stock_id=?";
        return CrudUtil.execute(sql, id);
    }

    public ResultSet findById(String id) throws Exception {
        String sql = "SELECT stock_id, name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path FROM Product WHERE stock_id=?";

        return CrudUtil.execute(sql, id);

    }

    public List<ProductDTO> findAll() throws Exception {
        String sql = "SELECT stock_id, name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path FROM Product ORDER BY name";

        List<ProductDTO> products = new ArrayList<>();

        try(ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }

        return products;
    }

    private ProductDTO mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("stock_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        double sellPrice = rs.getDouble("sell_price");
        String category = rs.getString("category");
        String condStr = rs.getString("p_condition");
        double buyPrice = rs.getDouble("buy_price");
        int warrantyMonth = rs.getInt("warranty_months");
        int qty = rs.getInt("qty");
        String imagePath = rs.getString("image_path");

        ProductCondition condition = fromConditionString(condStr);
        return new ProductDTO(id, name, description, sellPrice, category, condition, buyPrice, warrantyMonth, qty, imagePath);
    }

    private ProductCondition fromConditionString(String s) {
        if (s == null) return ProductCondition.BOTH;
        try {
            if(s.equalsIgnoreCase("USED")){
                return ProductCondition.USED;
            }else if(s.equalsIgnoreCase("BRAND NEW")){
                return ProductCondition.BRAND_NEW;
            }
            return ProductCondition.BOTH;
        } catch (IllegalArgumentException ex) {
            return ProductCondition.BOTH; // unknown condition value
        }
    }


}
