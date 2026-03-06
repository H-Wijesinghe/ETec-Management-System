package lk.ijse.etecmanagementsystem.entity;

import java.sql.Date;

public class ProductItem {
    private int item_id;
    private int stock_id;
    private int supplier_id;
    private String serial_number;
    private String status;
    private Date added_date;
    private int supplier_warranty_mo;
    private Date sold_date;
    private int customer_warranty_mo;

    public ProductItem() {
    }

    public ProductItem(int item_id, int stock_id, int supplier_id, String serial_number, String status, Date added_date, int supplier_warranty_mo, Date sold_date, int customer_warranty_mo) {
        this.item_id = item_id;
        this.stock_id = stock_id;
        this.supplier_id = supplier_id;
        this.serial_number = serial_number;
        this.status = status;
        this.added_date = added_date;
        this.supplier_warranty_mo = supplier_warranty_mo;
        this.sold_date = sold_date;
        this.customer_warranty_mo = customer_warranty_mo;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getStock_id() {
        return stock_id;
    }

    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getAdded_date() {
        return added_date;
    }

    public void setAdded_date(Date added_date) {
        this.added_date = added_date;
    }

    public int getSupplier_warranty_mo() {
        return supplier_warranty_mo;
    }

    public void setSupplier_warranty_mo(int supplier_warranty_mo) {
        this.supplier_warranty_mo = supplier_warranty_mo;
    }

    public Date getSold_date() {
        return sold_date;
    }

    public void setSold_date(Date sold_date) {
        this.sold_date = sold_date;
    }

    public int getCustomer_warranty_mo() {
        return customer_warranty_mo;
    }

    public void setCustomer_warranty_mo(int customer_warranty_mo) {
        this.customer_warranty_mo = customer_warranty_mo;
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "item_id=" + item_id +
                ", stock_id=" + stock_id +
                ", supplier_id=" + supplier_id +
                ", serial_number='" + serial_number + '\'' +
                ", status='" + status + '\'' +
                ", added_date=" + added_date +
                ", supplier_warranty_mo=" + supplier_warranty_mo +
                ", sold_date=" + sold_date +
                ", customer_warranty_mo=" + customer_warranty_mo +
                '}';
    }
}
