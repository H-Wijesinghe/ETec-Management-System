package lk.ijse.etecmanagementsystem.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductUtil {


    public  static final ObservableList<ProductDTO> productCache;
    static {
        productCache = FXCollections.observableArrayList();
    }




}
