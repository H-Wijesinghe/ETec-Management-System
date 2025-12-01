package lk.ijse.etecmanagementsystem.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductUtil {


    public static ObservableList<ProductDTO> productCache;
    static {
        productCache = FXCollections.observableArrayList();
        productCache.add(new ProductDTO("P001", "Laptop", "High performance laptop", 1200.00, "Electronics", ProductCondition.BRAND_NEW, 24, 10));
        productCache.add(new ProductDTO("P002", "Smartphone", "Latest model smartphone", 800.00, "Electronics", ProductCondition.USED, 12, 5));
        productCache.add(new ProductDTO("P003", "Headphones", "Noise-cancelling headphones", 150.00, "Accessories", ProductCondition.BRAND_NEW, 6, 20));
        productCache.add(new ProductDTO("P004", "Monitor", "4K UHD Monitor", 400.00, "Electronics", ProductCondition.BRAND_NEW, 18, 7));
        productCache.add(new ProductDTO("P005", "Keyboard", "Mechanical keyboard", 100.00, "Accessories", ProductCondition.USED, 3, 15));
    }


}
