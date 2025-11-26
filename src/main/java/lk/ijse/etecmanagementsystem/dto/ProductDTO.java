package lk.ijse.etecmanagementsystem.dto;

public class ProductDTO {
    private String name;
    private double price;
    private String category;
    private String imagePath;

    public ProductDTO(String name, double price, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }
}