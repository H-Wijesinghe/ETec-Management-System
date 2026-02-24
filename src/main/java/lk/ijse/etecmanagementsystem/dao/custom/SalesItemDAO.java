package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dao.entity.SalesItem;

import java.sql.SQLException;

public interface SalesItemDAO {
    boolean createSalesItem(SalesItem entity) throws SQLException;
}

