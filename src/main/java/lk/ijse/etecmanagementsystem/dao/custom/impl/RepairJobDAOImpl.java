package lk.ijse.etecmanagementsystem.dao.custom.impl;

import lk.ijse.etecmanagementsystem.dao.custom.RepairJobDAO;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.util.CrudUtil;
import lk.ijse.etecmanagementsystem.util.PaymentStatus;
import lk.ijse.etecmanagementsystem.util.RepairStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepairJobDAOImpl implements RepairJobDAO {

    public List<RepairJobDTO> getAllRepairJobs() throws SQLException {
        List<RepairJobDTO> list = new ArrayList<>();

        String sql = "SELECT * FROM RepairJob ORDER BY date_in DESC";


        ResultSet resultSet = CrudUtil.execute(sql);

        while (resultSet.next()) {
            RepairJobDTO dto = new RepairJobDTO(
                    resultSet.getInt("repair_id"),
                    resultSet.getInt("cus_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("device_name"),
                    resultSet.getString("device_sn"),
                    resultSet.getString("problem_desc"),
                    resultSet.getString("diagnosis_desc"),
                    resultSet.getString("repair_results"),
                    RepairStatus.valueOf(resultSet.getString("status")),
                    resultSet.getTimestamp("date_in"),
                    resultSet.getTimestamp("date_out"),
                    resultSet.getDouble("labor_cost"),
                    resultSet.getDouble("parts_cost"),
                    resultSet.getDouble("total_amount"),
                    resultSet.getDouble("paid_amount"),
                    resultSet.getDouble("discount"),
                    PaymentStatus.valueOf(resultSet.getString("payment_status"))
            );

            list.add(dto);
        }

        resultSet.close();
        return list;
    }

    public RepairJobDTO getRepairJobById(int repair_id) throws SQLException {
        String sql = "SELECT * FROM RepairJob WHERE repair_id = ?";
        ResultSet resultSet = CrudUtil.execute(sql, repair_id);

        if (resultSet.next()) {
            RepairJobDTO dto = new RepairJobDTO(
                    resultSet.getInt("repair_id"),
                    resultSet.getInt("cus_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("device_name"),
                    resultSet.getString("device_sn"),
                    resultSet.getString("problem_desc"),
                    resultSet.getString("diagnosis_desc"),
                    resultSet.getString("repair_results"),
                    RepairStatus.valueOf(resultSet.getString("status")),
                    resultSet.getTimestamp("date_in"),
                    resultSet.getTimestamp("date_out"),
                    resultSet.getDouble("labor_cost"),
                    resultSet.getDouble("parts_cost"),
                    resultSet.getDouble("total_amount"),
                    resultSet.getDouble("paid_amount"),
                    resultSet.getDouble("discount"),
                    PaymentStatus.valueOf(resultSet.getString("payment_status"))
            );
            resultSet.close();
            return dto;
        } else {
            resultSet.close();
            return null; // Not found
        }
    }

    public boolean saveRepairJob(RepairJobDTO dto) throws SQLException {
        String sql = "INSERT INTO RepairJob " +
                "(cus_id, user_id, device_name, device_sn, problem_desc, status, date_in, payment_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Date date;
        if (dto.getDateIn() != null) {
            date = new java.sql.Timestamp(dto.getDateIn().getTime());
        } else {
            date = new java.sql.Timestamp(System.currentTimeMillis());
        }
        return CrudUtil.execute(
                sql,
                dto.getCusId(),
                dto.getUserId(),
                dto.getDeviceName(),
                dto.getDeviceSn(),
                dto.getProblemDesc(),
                dto.getStatus().name(),
                date,
                "PENDING"
        );
    }

    public int getLastInsertedRepairId() throws SQLException {
        String idQuery = "SELECT LAST_INSERT_ID() AS id FROM RepairJob";
        ResultSet rs = CrudUtil.execute(idQuery);
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("Failed to retrieve Repair ID");
        }
    }

    public boolean updateRepairJob(RepairJobDTO dto) throws SQLException {
        String sql = "UPDATE RepairJob SET cus_id=?, device_name=?, device_sn=?, problem_desc=? WHERE repair_id=?";

        return CrudUtil.execute(
                sql,
                dto.getCusId(),
                dto.getDeviceName(),
                dto.getDeviceSn(),
                dto.getProblemDesc(),
                dto.getRepairId()

        );
    }


    public boolean updateRepairPayment(double amount, double totalAmount, double discount, String paymentStatus, int repairId) throws SQLException {
        String updateSql = "UPDATE RepairJob SET paid_amount = paid_amount + ?, total_amount = ?, discount = ?, payment_status = ? " +
                "WHERE repair_id = ?";
        return CrudUtil.execute(updateSql, amount,totalAmount, discount, paymentStatus, repairId);
    }

    public boolean updateRepairCosts(RepairJobDTO dto) throws SQLException {
        String sqlJob = "UPDATE RepairJob SET problem_desc=?, diagnosis_desc=?, repair_results=?, " +
                "labor_cost=?, parts_cost=?, total_amount=? WHERE repair_id=?";

        return CrudUtil.execute(
                sqlJob,
                dto.getProblemDesc(),
                dto.getDiagnosisDesc(),
                dto.getRepairResults(),
                dto.getLaborCost(),
                dto.getPartsCost(),
                dto.getTotalAmount(),
                dto.getRepairId()
        );
    }

    public boolean updateStatus(int repairId, RepairStatus newStatus) throws SQLException {
        String sql = "UPDATE RepairJob SET status = ? WHERE repair_id = ?";

        return CrudUtil.execute(sql, newStatus.name(), repairId);
    }

    public boolean updateDateOut(String paymentStatus, int repairId) throws SQLException {
        String sqlUpdateJob = "UPDATE RepairJob SET status='DELIVERED', date_out=NOW(), payment_status=? WHERE repair_id=?";
        return  CrudUtil.execute(sqlUpdateJob, paymentStatus, repairId);
    }

    public boolean deleteRepairJob(int repairId) throws SQLException {
        String sql = "DELETE FROM RepairJob WHERE repair_id=?";

        return CrudUtil.execute(sql, repairId);
    }
}
