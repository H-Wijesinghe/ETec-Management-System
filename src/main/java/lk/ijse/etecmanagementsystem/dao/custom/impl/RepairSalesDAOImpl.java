package lk.ijse.etecmanagementsystem.dao.custom.impl;

import lk.ijse.etecmanagementsystem.dao.custom.RepairSalesDAO;
import lk.ijse.etecmanagementsystem.util.CrudUtil;

public class RepairSalesDAOImpl implements RepairSalesDAO {
    public  boolean saveRepairSale(int repairId, int saleId) throws Exception {
        String sqlLink = "INSERT INTO RepairSale (repair_id, sale_id) VALUES (?, ?)";
        return  CrudUtil.execute(sqlLink, repairId, saleId);
    }
}
