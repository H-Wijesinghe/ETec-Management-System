package lk.ijse.etecmanagementsystem.model;

import lk.ijse.etecmanagementsystem.dto.CategoryDTO;
import lk.ijse.etecmanagementsystem.util.CrudUtil;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryModel {

    public List<String> getAllCategories() throws  Exception {
        String sql = "SELECT * FROM Category";
        ResultSet rs = CrudUtil.execute(sql);
        List<String> list = new ArrayList();
        while (rs.next()) {
            list.add(rs.getString("category_name"));
        }
        return list;

    }
}
