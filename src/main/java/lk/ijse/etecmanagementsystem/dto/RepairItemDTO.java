package lk.ijse.etecmanagementsystem.dto;

public class RepairItemDTO {
    private int id;
    private int repairId;
    private int itemId;
    private double unitPrice;

    public RepairItemDTO() {
    }

    public RepairItemDTO(int id, int repairId, int itemId, double unitPrice) {
        this.id = id;
        this.repairId = repairId;
        this.itemId = itemId;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepairId() {
        return repairId;
    }

    public void setRepairId(int repairId) {
        this.repairId = repairId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
