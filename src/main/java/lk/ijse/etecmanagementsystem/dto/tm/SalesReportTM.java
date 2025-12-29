package lk.ijse.etecmanagementsystem.dto.tm;


import java.sql.Date;

public class SalesReportTM {
    private int saleId;
    private Date date;
    private String customerName;
    private double grandTotal;
    private String paymentStatus;

    public SalesReportTM(int saleId, Date date, String customerName, double grandTotal, String paymentStatus) {
        this.saleId = saleId;
        this.date = date;
        this.customerName = customerName;
        this.grandTotal = grandTotal;
        this.paymentStatus = paymentStatus;
    }

    public int getSaleId() { return saleId; }
    public Date getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public double getGrandTotal() { return grandTotal; }
    public String getPaymentStatus() { return paymentStatus; }
}