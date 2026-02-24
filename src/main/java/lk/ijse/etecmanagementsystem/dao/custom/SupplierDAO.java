package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dto.SupplierDTO;

import java.sql.SQLException;
import java.util.List;

public interface SupplierDAO {
    List<SupplierDTO> getAllSuppliers() throws SQLException;

    SupplierDTO getSupplierById(int id) throws SQLException;

    List<SupplierDTO> getSupplierByName(String name) throws SQLException;

    boolean saveSuppliers(SupplierDTO supplier) throws SQLException;

    boolean updateSuppliers(SupplierDTO supplier) throws SQLException;

    boolean deleteSuppliers(int id) throws SQLException;
}

