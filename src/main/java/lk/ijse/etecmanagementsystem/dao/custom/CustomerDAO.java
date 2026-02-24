package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dto.CustomerDTO;

import java.sql.SQLException;
import java.util.List;

public interface CustomerDAO {
    List<CustomerDTO> getAllCustomers() throws SQLException;

    CustomerDTO getCustomerById(int id) throws SQLException;

    boolean saveCustomer(CustomerDTO customer) throws SQLException;

    int insertCustomerAndGetId(CustomerDTO customer) throws SQLException;

    boolean updateCustomer(CustomerDTO customer) throws SQLException;

    boolean deleteCustomer(int id) throws SQLException;
}

