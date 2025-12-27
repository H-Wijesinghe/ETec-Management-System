package lk.ijse.etecmanagementsystem.dto.tm;


public class PendingRepairTM {
    private int repairId;
    private String device;
    private String customerName;
    private double balanceDue;

    public PendingRepairTM(int repairId, String device, String customerName, double balanceDue) {
        this.repairId = repairId;
        this.device = device;
        this.customerName = customerName;
        this.balanceDue = balanceDue;
    }

    public int getRepairId() { return repairId; }
    public String getDevice() { return device; }
    public String getCustomerName() { return customerName; }
    public double getBalanceDue() { return balanceDue; }

    public void setBalanceDue(double balanceDue) { this.balanceDue = balanceDue; }
}