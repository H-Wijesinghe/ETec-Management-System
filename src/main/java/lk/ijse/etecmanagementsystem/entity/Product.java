package lk.ijse.etecmanagementsystem.entity;

public class Product {
    int stock_id;
    String name;
    String description;
    String category;
    String p_condition;
    int qty;
    int warranty_months;
    String image_path;
    double buy_price;
    double sell_price;

    public Product() {
    }

    public Product(int stock_id, String name, String description, String category, String p_condition, int qty, int warranty_months, String image_path, double buy_price, double sell_price) {
        this.stock_id = stock_id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.p_condition = p_condition;
        this.qty = qty;
        this.warranty_months = warranty_months;
        this.image_path = image_path;
        this.buy_price = buy_price;
        this.sell_price = sell_price;
    }

    public int getStock_id() {
        return stock_id;
    }

    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getP_condition() {
        return p_condition;
    }

    public void setP_condition(String p_condition) {
        this.p_condition = p_condition;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getWarranty_months() {
        return warranty_months;
    }

    public void setWarranty_months(int warranty_months) {
        this.warranty_months = warranty_months;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public double getBuy_price() {
        return buy_price;
    }

    public void setBuy_price(double buy_price) {
        this.buy_price = buy_price;
    }

    public double getSell_price() {
        return sell_price;
    }

    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "stock_id=" + stock_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", p_condition='" + p_condition + '\'' +
                ", qty=" + qty +
                ", warranty_months=" + warranty_months +
                ", image_path='" + image_path + '\'' +
                ", buy_price=" + buy_price +
                ", sell_price=" + sell_price +
                '}';
    }
}
