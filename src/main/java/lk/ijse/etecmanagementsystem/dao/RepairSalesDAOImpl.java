package lk.ijse.etecmanagementsystem.dao;

import lk.ijse.etecmanagementsystem.util.CrudUtil;

import java.sql.PreparedStatement;

public class RepairSalesDAOImpl {
    public  boolean saveRepairSale(int repairId, int saleId) throws Exception {
        String sqlLink = "INSERT INTO RepairSale (repair_id, sale_id) VALUES (?, ?)";
        return  CrudUtil.execute(sqlLink, repairId, saleId);
    }
}
