package lk.ijse.etecmanagementsystem.bo.custom;

import lk.ijse.etecmanagementsystem.bo.SuperBO;
import java.sql.SQLException;
import java.util.List;

public interface CategoryBO extends SuperBO {
    List<String> getAllCategories() throws SQLException;

    boolean saveCategory(String category) throws SQLException;

    boolean updateCategory(String newName, String oldName) throws SQLException;

    boolean deleteCategory(String categoryName) throws SQLException;
}
