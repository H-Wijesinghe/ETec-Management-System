package lk.ijse.etecmanagementsystem.dto;

import lk.ijse.etecmanagementsystem.util.ProductCondition;

public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private double sellPrice;
    private String category;
    private ProductCondition condition;
    private double buyPrice;
    private int warrantyMonth;
    private int qty;
    private String imagePath;

    public ProductDTO() {
    }

    public ProductDTO(String id, String name, String description, double sellPrice, String category, ProductCondition condition, double buyPrice, int warrantyMonth, int qty, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sellPrice = sellPrice;
        this.category = category;
        this.condition = condition;
        this.buyPrice = buyPrice;
        this.warrantyMonth = warrantyMonth;
        this.qty = qty;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ProductCondition getCondition() {
        return condition;
    }

    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getWarrantyMonth() {
        return warrantyMonth;
    }

    public void setWarrantyMonth(int warrantyMonth) {
        this.warrantyMonth = warrantyMonth;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sellPrice=" + sellPrice +
                ", category='" + category + '\'' +
                ", condition=" + condition +
                ", buyPrice=" + buyPrice +
                ", warrantyMonth=" + warrantyMonth +
                ", qty=" + qty +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}