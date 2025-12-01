package lk.ijse.etecmanagementsystem.service;

import javafx.collections.ObservableList;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.util.ProductCondition;
import lk.ijse.etecmanagementsystem.util.ProductUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static lk.ijse.etecmanagementsystem.util.ProductCondition.BOTH;
import static lk.ijse.etecmanagementsystem.util.ProductCondition.USED;

public class InventoryService {

    private final List<ProductDTO> masterList = new ArrayList<>();

    public InventoryService() {
        masterList.addAll(ProductUtil.productCache);
    }

    private void loadDummyData() {
        for (int i = 1; i <= 500; i++) {
            String cat = (i % 3 == 0) ? "Electronics" : (i % 2 == 0) ? "Accessories" : "Parts";
            masterList.add(new ProductDTO(
                    "P" + String.format("%03d", i),
                    "Product " + i,
                    "Description for Product " + i,
                    50.0 + i,
                    cat,
                    (i % 2 == 0) ? ProductCondition.BRAND_NEW : USED,
                    12,
                    100 + i
            ));
        }
    }

    public List<ProductDTO> getFilteredProducts(String searchText, String category, ProductCondition value) {

        String finalSearch = (searchText == null) ? "" : searchText.toLowerCase();
        String finalCategory = (category == null) ? "" : category;

        return masterList.stream()

                .filter(p -> p.getName().toLowerCase().contains(finalSearch))


                .filter(p -> isCategoryMatch(p, finalCategory))


                .filter(p -> isConditionMatch(p, value))


                .sorted(Comparator.comparing(ProductDTO::getName))
                .collect(Collectors.toList());
    }


    private boolean isCategoryMatch(ProductDTO p, String category) {

        return category == null
                || category.equals("All Categories")
                || p.getCategory().equals(category);
    }


    private boolean isConditionMatch(ProductDTO p, ProductCondition value) {

        return value == null
                || value == ProductCondition.BOTH
                || p.getCondition() == value;
    }
}