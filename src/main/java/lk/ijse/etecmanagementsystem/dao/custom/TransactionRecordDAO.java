package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dao.entity.TransactionRecord;
import lk.ijse.etecmanagementsystem.dto.tm.TransactionTM;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface TransactionRecordDAO {
    boolean insertTransactionRecord(TransactionRecord entity) throws SQLException;

    List<TransactionTM> getAllTransactions(Date dpFromDate, Date dpToDate) throws SQLException;

    boolean saveManualTransaction(String type, double amount, String method, String note, int userId) throws SQLException;

    double[] getDashboardStats(Date fromDate, Date toDate) throws SQLException;

    boolean settleTransaction(TransactionRecord entity, String type) throws SQLException;
}

