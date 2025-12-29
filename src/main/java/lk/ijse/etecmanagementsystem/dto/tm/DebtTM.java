package lk.ijse.etecmanagementsystem.dto.tm;

public class DebtTM {
    private final int id;
    private final String type;
    private final String customer;
    private final double amount;

    public DebtTM(int id, String type, String customer, double amount) {
        this.id = id;
        this.type = type;
        this.customer = customer;
        this.amount = amount;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getCustomer() { return customer; }
    public double getAmount() { return amount; }
}