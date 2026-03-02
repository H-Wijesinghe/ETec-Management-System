package lk.ijse.etecmanagementsystem.dao.custom.impl;

import lk.ijse.etecmanagementsystem.dao.custom.SalesItemDAO;
import lk.ijse.etecmanagementsystem.entity.SalesItem;
import lk.ijse.etecmanagementsystem.dao.CrudUtil;

import java.sql.SQLException;

public class SalesItemDAOImpl implements SalesItemDAO {
    public boolean createSalesItem(SalesItem entity) throws SQLException {
        String sqlSalesItem = "INSERT INTO SalesItem (sale_id, item_id, customer_warranty_months, unit_price, discount) VALUES (?, ?, ?, ?, ?)";
        return CrudUtil.execute(sqlSalesItem,
                entity.getSale_id(),
                entity.getItem_id(),
                entity.getCustomer_warranty_months(),
                entity.getUnit_price(),
                entity.getDiscount()
        );
    }
}
