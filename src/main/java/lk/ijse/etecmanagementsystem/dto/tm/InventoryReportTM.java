package lk.ijse.etecmanagementsystem.dto.tm;

public class InventoryReportTM {
    private String stockId;
    private String name;
    private String category;
    private int qty;

    public InventoryReportTM(String stockId, String name, String category, int qty) {
        this.stockId = stockId;
        this.name = name;
        this.category = category;
        this.qty = qty;
    }

    // Getters needed for PropertyValueFactory
    public String getStockId() { return stockId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQty() { return qty; }
}