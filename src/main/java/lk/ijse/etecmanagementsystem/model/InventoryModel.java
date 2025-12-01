package lk.ijse.etecmanagementsystem.model;

import javafx.scene.control.Alert;
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

public class InventoryModel {
    /**
     * Saves a new product.
     * Returns true if one row was inserted.
     */
    public boolean save(ProductDTO p) throws Exception {
        String sql = "INSERT INTO Product (stock_id, name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bindProduct(ps, p);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Updates a product row by id.
     * Returns true if one row was updated.
     */
    public boolean update(ProductDTO p) throws Exception {
        String sql = "UPDATE Product SET name=?, description=?, sell_price=?, category=?, p_condition=?, buy_price=?, warranty_months=?, qty=?, image_path=? WHERE stock_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // set fields except id, then id at the end
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getSellPrice());
            ps.setString(4, p.getCategory());
            ps.setString(5, toConditionString(p.getCondition()));
            // buyPrice may be 0.0 if not provided
            ps.setDouble(6, p.getBuyPrice());
            ps.setInt(7, p.getWarrantyMonth());
            ps.setInt(8, p.getQty());
            ps.setString(9, p.getImagePath());
            ps.setString(10, p.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Deletes a product by id.
     */
    public boolean deleteById(String id) throws Exception {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        }
    }


    /**
     * Finds a product by id.
     * Returns the ProductDTO if found, otherwise null.
     */
    public ResultSet findById(String id) throws Exception {
        String sql = "SELECT stock_id, name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path FROM Product WHERE id=?";

        return CrudUtil.execute(sql, id);
    }

    /**
     * Returns all products sorted by name.
     */
    public List<ProductDTO> findAll() throws Exception {
        String sql = "SELECT stock_id, name, description, sell_price, category, p_condition, buy_price, warranty_months, qty, image_path FROM Product ORDER BY name";

            ResultSet rs = CrudUtil.execute(sql);
            List<ProductDTO> products = new ArrayList<>();
            while (rs.next()) {
                products.add(mapRow(rs));
            }
            return products;
    }

    // Helpers
    private void bindProduct(PreparedStatement ps, ProductDTO p) throws SQLException {
        ps.setString(1, p.getId());
        ps.setString(2, p.getName());
        ps.setString(3, p.getDescription());
        ps.setDouble(4, p.getSellPrice());
        ps.setString(5, p.getCategory());
        ps.setString(6, toConditionString(p.getCondition()));
        ps.setDouble(7, p.getBuyPrice());
        ps.setInt(8, p.getWarrantyMonth());
        ps.setInt(9, p.getQty());
        ps.setString(10, p.getImagePath());
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

    private String toConditionString(ProductCondition condition) {
        return condition == null ? null : condition.name();
    }

    private ProductCondition fromConditionString(String s) {
        if (s == null) return null;
        try {
            if(s.equals("Used")){
                return ProductCondition.USED;
            }else if(s.equals("Brand New")){
                return ProductCondition.BRAND_NEW;
            }
            return ProductCondition.BOTH;
        } catch (IllegalArgumentException ex) {
            return ProductCondition.BOTH; // unknown condition value
        }
    }
}
