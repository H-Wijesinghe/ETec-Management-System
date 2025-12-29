package lk.ijse.etecmanagementsystem.dto.tm;

public class ManualTransactionResult {
    private final String type;
    private final double amount;
    private final String method;
    private final String note;

    public ManualTransactionResult(String type, double amount, String method, String note) {
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.note = note;
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getNote() { return note; }
}