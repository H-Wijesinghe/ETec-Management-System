package lk.ijse.etecmanagementsystem.dao.custom;

import javafx.collections.ObservableList;
import lk.ijse.etecmanagementsystem.dto.SalesDTO;
import lk.ijse.etecmanagementsystem.dto.tm.PendingSaleTM;
import lk.ijse.etecmanagementsystem.dto.tm.SalesTM;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface SalesDAO {
    List<SalesTM> getAllSales() throws SQLException;

    SalesDTO getSaleById(int saleId) throws SQLException;

    List<SalesTM> getSalesByDateRange(LocalDate from, LocalDate to) throws SQLException;

    ObservableList<PendingSaleTM> getPendingSales() throws SQLException;

    boolean saveSale(SalesDTO salesDTO) throws SQLException;

    int getLastInsertedSalesId() throws SQLException;

    boolean updateSalePayment(int saleId, double newPaidAmount, String newPaymentStatus) throws SQLException;
}

