package lk.ijse.etecmanagementsystem.dto.tm;


public class TransactionTM {
    private int transactionId;
    private String date;
    private String type;
    private String reference;
    private String flow;
    private double amount;
    private String user;

    public TransactionTM(int transactionId, String date, String type, String reference, String flow, double amount, String user) {
        this.transactionId = transactionId;
        this.date = date;
        this.type = type;
        this.reference = reference;
        this.flow = flow;
        this.amount = amount;
        this.user = user;
    }

    // Getters are required for PropertyValueFactory to work
    public int getTransactionId() { return transactionId; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getReference() { return reference; }
    public String getFlow() { return flow; }
    public double getAmount() { return amount; }
    public String getUser() { return user; }
}