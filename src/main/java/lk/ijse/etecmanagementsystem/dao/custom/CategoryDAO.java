package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dao.CrudDAO;
import lk.ijse.etecmanagementsystem.entity.Category;

import java.sql.SQLException;

public interface CategoryDAO extends CrudDAO<Category> {
 boolean updateCategoryName(String newName, String oldName) throws SQLException ;
 boolean deleteCategory(String categoryName) throws SQLException ;

}
