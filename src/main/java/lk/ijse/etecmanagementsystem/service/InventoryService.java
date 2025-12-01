//package lk.ijse.etecmanagementsystem.service;
//
//import javafx.collections.ObservableList;
//import lk.ijse.etecmanagementsystem.dto.ProductDTO;
//import lk.ijse.etecmanagementsystem.util.ProductCondition;
//import lk.ijse.etecmanagementsystem.util.ProductUtil;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static lk.ijse.etecmanagementsystem.util.ProductCondition.*;
//
//public class InventoryService {
//
//    private final List<ProductDTO> masterList = new ArrayList<>();
//
//    public InventoryService() {
//        masterList.addAll(ProductUtil.productCache);
//    }
//
//    private void loadDummyData() {
//        for (int i = 1; i <= 500; i++) {
//            String cat = (i % 3 == 0) ? "Electronics" : (i % 2 == 0) ? "Accessories" : "Parts";
//            masterList.add(new ProductDTO(
//                    "P" + String.format("%03d", i),
//                    "Product " + i,
//                    "Description for Product " + i,
//                    50.0 + i,
//                    cat,
//                    (i % 2 == 0) ? ProductCondition.BRAND_NEW : USED,
//                    12,
//                    100 + i
//            ));
//        }
//    }
//
//    public List<ProductDTO> getFilteredProducts(String searchText, String category, ProductCondition value) {
//
//        String finalSearch = (searchText == null) ? "" : searchText.toLowerCase();
//
//        return masterList.stream()
//
//                .filter(p -> p.getName().toLowerCase().contains(finalSearch))
//
//
//                .filter(p -> isCategoryMatch(p, category))
//
//
//                .filter(p -> isConditionMatch(p, value))
//
//
//                .sorted(Comparator.comparing(ProductDTO::getName))
//                .collect(Collectors.toList());
//    }
//
//
//    private boolean isCategoryMatch(ProductDTO p, String category) {
//
//        return category == null
//                || category.equals("All Categories")
//                || p.getCategory().equals(category);
//    }
//
//
//    private boolean isConditionMatch(ProductDTO p, ProductCondition value) {
//
//        if(p.getCondition().equals(value)) {
//            return true;
//        } else if(value == BOTH || value == null) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//}
package lk.ijse.etecmanagementsystem.service;

import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.util.ProductCondition;
import lk.ijse.etecmanagementsystem.util.ProductUtil;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryService {

    // Removed 'masterList' to prevent stale data.
    // We will stream directly from the static ProductUtil.productCache

    public List<ProductDTO> getFilteredProducts(String searchText, String category, ProductCondition conditionFilter) {

        String finalSearch = (searchText == null) ? "" : searchText.toLowerCase();

        return ProductUtil.productCache.stream()
                // 1. Filter by Name (or ID)
                .filter(p -> p.getName().toLowerCase().contains(finalSearch)
//                        || p.getId().toLowerCase().contains(finalSearch)
                )

                // 2. Filter by Category
                .filter(p -> isCategoryMatch(p, category))

                // 3. Filter by Condition (Enum)
                .filter(p -> isConditionMatch(p, conditionFilter))

                .collect(Collectors.toList());
    }

    private boolean isCategoryMatch(ProductDTO p, String category) {
        // If category is null or "All Categories", return true (show all)
        return category == null
                || category.equals("All Categories")
                || p.getCategory().equals(category);
    }

    private boolean isConditionMatch(ProductDTO p, ProductCondition filterValue) {
        // 1. If filter is "BOTH" (the wildcard) or null, return true (show all)
        if (filterValue == null || filterValue == ProductCondition.BOTH) {
            return true;
        }

        // 2. Safety check: If product has no condition set, decide if you want to show it.
        // Returning false hides invalid data.
        if (p.getCondition() == null) {
            return false;
        }

        // 3. Strict match (e.g., USED == USED)
        // Note: '==' works fine for Enums and avoids NullPointerException
        return p.getCondition() == filterValue;
    }
}