package lk.ijse.etecmanagementsystem.dao;

import lk.ijse.etecmanagementsystem.dto.ProductItemDTO;
import lk.ijse.etecmanagementsystem.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductItemDAOImpl {
    public boolean updateCustomerWarranty(int customerWarranty, int stockId) throws SQLException {
        String sqlItem = "UPDATE ProductItem SET customer_warranty_mo = ? WHERE stock_id = ? AND status = 'AVAILABLE'";
        return CrudUtil.execute(sqlItem, customerWarranty, stockId);
    }

    public int getRealItemCount(int stockId) throws SQLException {
        System.out.println("DEBUG: Querying Real Item Count for Stock ID: " + stockId);

        String sql = "SELECT COUNT(*) AS count FROM ProductItem WHERE stock_id = ? AND serial_number NOT LIKE 'PENDING-%' AND status = 'AVAILABLE'";

        ResultSet rs = CrudUtil.execute(sql, stockId);
        if(rs.next()) {
            int count = rs.getInt("count");
            System.out.println("DEBUG: Real Item Count for Stock ID " + stockId + " is " + count);
            return count;
        } else {
            return 0;
        }
    }
}
