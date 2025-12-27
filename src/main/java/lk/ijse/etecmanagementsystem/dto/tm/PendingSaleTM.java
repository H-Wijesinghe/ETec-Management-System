package lk.ijse.etecmanagementsystem.dto.tm;


public class PendingSaleTM {
    private int saleId;
    private String customerName;
    private double total;
    private double balanceDue;

    public PendingSaleTM(int saleId, String customerName, double total, double balanceDue) {
        this.saleId = saleId;
        this.customerName = customerName;
        this.total = total;
        this.balanceDue = balanceDue;
    }

    public int getSaleId() { return saleId; }
    public String getCustomerName() { return customerName; }
    public double getTotal() { return total; }
    public double getBalanceDue() { return balanceDue; }

    // Setters are optional unless you plan to edit data directly in the table cells
    public void setBalanceDue(double balanceDue) { this.balanceDue = balanceDue; }
}