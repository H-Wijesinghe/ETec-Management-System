package lk.ijse.etecmanagementsystem.model;


import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.util.CrudUtil;
import lk.ijse.etecmanagementsystem.util.ProductCondition;

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

    public boolean update(ProductDTO p) throws Exception {
        String sql = "UPDATE Product SET name=?, description=?, sell_price=?, category=?, p_condition=?, buy_price=?, warranty_months=?, qty=?, image_path=? WHERE stock_id=?";

        return CrudUtil.execute(sql,
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

        ResultSet rs = CrudUtil.execute(sql);
        List<ProductDTO> products = new ArrayList<>();
        while (rs.next()) {
            products.add(mapRow(rs));
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
        if (s == null) return null;
        try {
            if(s.equals("Used")){
                return ProductCondition.USED;
            }else if(s.equals("Brand New")){
                return ProductCondition.BRAND_NEW;
            }
            return null;
        } catch (IllegalArgumentException ex) {
            return null; // unknown condition value
        }
    }


}
