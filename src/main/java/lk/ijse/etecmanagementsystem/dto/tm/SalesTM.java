package lk.ijse.etecmanagementsystem.dto.tm;




public class SalesTM {
    private int saleId;
    private String customerName;
    private String userName;
    private String description;
    private double subTotal;
    private double discount;
    private double grandTotal;
    private double paidAmount;

    public SalesTM() {
    }


    public SalesTM(int saleId, String customerName, String userName, String description, double subTotal, double discount, double grandTotal, double paidAmount) {
        this.saleId = saleId;
        this.customerName = customerName;
        this.userName = userName;
        this.description = description;
        this.subTotal = subTotal;
        this.discount = discount;
        this.grandTotal = grandTotal;
        this.paidAmount = paidAmount;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
}