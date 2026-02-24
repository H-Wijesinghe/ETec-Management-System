package lk.ijse.etecmanagementsystem.dao.custom;

import java.util.List;

public interface CategoryDAO {
    List<String> getAllCategories() throws Exception;

    boolean saveCategory(String category) throws Exception;

    boolean updateCategory(String newName, String oldName) throws Exception;

    boolean deleteCategory(String categoryName) throws Exception;
}
