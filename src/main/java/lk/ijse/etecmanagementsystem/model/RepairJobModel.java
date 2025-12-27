package lk.ijse.etecmanagementsystem.model;


import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.dto.tm.RepairJobTM;
import lk.ijse.etecmanagementsystem.dto.tm.RepairPartTM;
import lk.ijse.etecmanagementsystem.util.PaymentStatus;
import lk.ijse.etecmanagementsystem.util.ProductCondition;
import lk.ijse.etecmanagementsystem.util.RepairStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairJobModel {

    public List<RepairJobTM> getAllRepairJobs() throws SQLException {
        List<RepairJobTM> list = new ArrayList<>();

        // We assume you have a DBConnection class
        Connection connection = DBConnection.getInstance().getConnection();

        // JOIN Query to get Customer details along with Repair Job
        String sql = "SELECT r.repair_id, r.cus_id, r.user_id, r.device_name, r.device_sn, " +
                "r.problem_desc, r.diagnosis_desc, r.repair_results, " + // <--- Added here
                "r.status, r.date_in, r.date_out, r.labor_cost, r.parts_cost, r.total_amount, r.discount, r.payment_status, r.paid_amount, " +
                "c.name AS cus_name, c.number AS cus_contact, " +
                "c.email AS cus_email, c.address AS cus_address " +
                "FROM RepairJob r " +
                "JOIN Customer c ON r.cus_id = c.cus_id " +
                "ORDER BY r.date_in DESC";

        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            // 1. Create the DTO from the Result Set
            RepairJobDTO dto = new RepairJobDTO(
                    resultSet.getInt("repair_id"),
                    resultSet.getInt("cus_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("device_name"),
                    resultSet.getString("device_sn"),
                    resultSet.getString("problem_desc"),
                    resultSet.getString("diagnosis_desc"), // <--- Get New Col
                    resultSet.getString("repair_results"), // <--- Get New Col
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

            // 2. Extract Customer Info (Not in DTO, but needed for TM)
            String cusName = resultSet.getString("cus_name");
            String cusContact = resultSet.getString("cus_contact");
            String email = resultSet.getString("cus_email");
            String address = resultSet.getString("cus_address");

            // 3. Create the TM
            RepairJobTM tm = new RepairJobTM(dto, cusName, cusContact, email, address);
            list.add(tm);
        }

        return list;
    }


    public boolean saveRepairJob(RepairJobDTO dto) throws SQLException {
        String sql = "INSERT INTO RepairJob " +
                "(cus_id, user_id, device_name, device_sn, problem_desc, status, date_in, payment_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pstm.setInt(1, dto.getCusId());
        pstm.setInt(2, dto.getUserId());
        pstm.setString(3, dto.getDeviceName());
        pstm.setString(4, dto.getDeviceSn());
        pstm.setString(5, dto.getProblemDesc());
        pstm.setString(6, dto.getStatus().name()); // ENUM to String

        // Handle Date Conversion (Java Date -> SQL Timestamp)
        if (dto.getDateIn() != null) {
            pstm.setTimestamp(7, new java.sql.Timestamp(dto.getDateIn().getTime()));
        } else {
            pstm.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
        }

        // Default Payment Status
        pstm.setString(8, "PENDING");

        if (pstm.executeUpdate() <= 0) {
            return false;
        }

        // Get the generated Sale ID
        int repairId = -1;
        ResultSet rs = pstm.getGeneratedKeys();
        if (rs.next()) {
            repairId = rs.getInt(1);
        }
        if(repairId > 0) {
            dto.setRepairId(repairId); // Set the generated ID back to DTO
            return true;
        }
        return false;
    }

    public boolean updateRepairJob(RepairJobDTO dto) throws SQLException {
        String sql = "UPDATE RepairJob SET cus_id=?, device_name=?, device_sn=?, problem_desc=? WHERE repair_id=?";

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setInt(1, dto.getCusId());
        pstm.setString(2, dto.getDeviceName());
        pstm.setString(3, dto.getDeviceSn());
        pstm.setString(4, dto.getProblemDesc());
        pstm.setInt(5, dto.getRepairId());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteRepairJob(int repairId) throws SQLException {
        String sql = "DELETE FROM RepairJob WHERE repair_id=?";

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setInt(1, repairId);

        return pstm.executeUpdate() > 0;
    }

    // 2. NEW METHOD: To save the text from the 3 Tabs
    public boolean updateRepairDescriptions(int repairId, String diagnosis, String results) throws SQLException {
        String sql = "UPDATE RepairJob SET diagnosis_desc = ?, repair_results = ? WHERE repair_id = ?";
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, diagnosis);
        pstm.setString(2, results);
        pstm.setInt(3, repairId);
        return pstm.executeUpdate() > 0;
    }

    // Add update methods here later (e.g., updateStatus)
    public boolean updateStatus(int repairId, RepairStatus newStatus) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "UPDATE RepairJob SET status = ? WHERE repair_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, newStatus.name());
        pstm.setInt(2, repairId);
        return pstm.executeUpdate() > 0;
    }

    // 1. GET USED PARTS (Load from RepairItem table)
    public List<RepairPartTM> getUsedParts(int repairId) throws SQLException {
        List<RepairPartTM> list = new ArrayList<>();

        // JOIN: RepairItem -> ProductItem -> Product (To get Name & Price)
        String sql = "SELECT pi.item_id, p.name, pi.serial_number, p.p_condition, p.sell_price " +
                "FROM RepairItem ri " +
                "JOIN ProductItem pi ON ri.item_id = pi.item_id " +
                "JOIN Product p ON pi.stock_id = p.stock_id " +
                "WHERE ri.repair_id = ?";

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, repairId);
        ResultSet rs = pstm.executeQuery();

        while (rs.next()) {


            list.add(new RepairPartTM(
                    rs.getInt("item_id"),
                    rs.getString("name"),
                    rs.getString("serial_number"),
                    fromConditionString(rs.getString("p_condition")),
                    rs.getDouble("sell_price")
            ));
        }
        return list;
    }

    // 2. SAVE JOB & PARTS (Transactional)
    public boolean updateRepairJobDetails(int repairId, String intake, String diagnosis, String resolution,
                                          double laborCost, double partsCost, double totalAmount,
                                          List<RepairPartTM> activeParts,
                                          List<RepairPartTM> returnedParts) throws SQLException {

        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // START TRANSACTION

            // A. Update RepairJob Text & Costs
            String sqlJob = "UPDATE RepairJob SET problem_desc=?, diagnosis_desc=?, repair_results=?, " +
                    "labor_cost=?, parts_cost=?, total_amount=? WHERE repair_id=?";
            PreparedStatement pstmJob = connection.prepareStatement(sqlJob);
            pstmJob.setString(1, intake);
            pstmJob.setString(2, diagnosis);
            pstmJob.setString(3, resolution);
            pstmJob.setDouble(4, laborCost);
            pstmJob.setDouble(5, partsCost);
            pstmJob.setDouble(6, totalAmount);
            pstmJob.setInt(7, repairId);
            pstmJob.executeUpdate();

            // B. ADD NEW PARTS
            // We check if the link already exists to avoid duplicates
            String sqlCheck = "SELECT id FROM RepairItem WHERE repair_id=? AND item_id=?";
            String sqlInsertLink = "INSERT INTO RepairItem (repair_id, item_id) VALUES (?, ?)";
            String sqlMarkSold = "UPDATE ProductItem SET status='IN_REPAIR_USE', sold_date=NOW() WHERE item_id=?";
            String sqlFixPlaceholders = "UPDATE ProductItem " +
                    "SET serial_number = CONCAT('REPAIR-', SUBSTRING(serial_number, 9)) " +
                    "WHERE item_id=? AND serial_number LIKE 'PENDING-%'";

            PreparedStatement pstmCheck = connection.prepareStatement(sqlCheck);
            PreparedStatement pstmLink = connection.prepareStatement(sqlInsertLink);
            PreparedStatement pstmSold = connection.prepareStatement(sqlMarkSold);
            PreparedStatement pstmFixSn = connection.prepareStatement(sqlFixPlaceholders);

            for (RepairPartTM part : activeParts) {
                // Check duplicate
                pstmCheck.setInt(1, repairId);
                pstmCheck.setInt(2, part.getItemId());
                if (!pstmCheck.executeQuery().next()) {
                    // 1. Insert Link in RepairItem
                    pstmLink.setInt(1, repairId);
                    pstmLink.setInt(2, part.getItemId());
                    pstmLink.addBatch();

                    // 2. Mark Stock as SOLD in ProductItem
                    pstmSold.setInt(1, part.getItemId());
                    pstmSold.addBatch();

                    // 3. Fix Placeholder Serial Numbers
                    pstmFixSn.setInt(1, part.getItemId());
                    pstmFixSn.addBatch();
                }
            }
            pstmLink.executeBatch();
            pstmSold.executeBatch();
            pstmFixSn.executeBatch();




            // C. REMOVE RETURNED PARTS (Restock)
            if (!returnedParts.isEmpty()) {
                String sqlDeleteLink = "DELETE FROM RepairItem WHERE repair_id=? AND item_id=?";
                String sqlRestock = "UPDATE ProductItem SET status='AVAILABLE', sold_date=NULL WHERE item_id=?";
                String sqlReplacePlaceholders = "UPDATE ProductItem " +
                        "SET serial_number = CONCAT('PENDING-', SUBSTRING(serial_number, 8)) " +
                        "WHERE item_id=? AND serial_number LIKE 'REPAIR-%'";

                try (
                        PreparedStatement pstmDel = connection.prepareStatement(sqlDeleteLink);
                        PreparedStatement pstmStock = connection.prepareStatement(sqlRestock);
                        PreparedStatement pstmReplaceSn = connection.prepareStatement(sqlReplacePlaceholders)

                ){
                    for (RepairPartTM part : returnedParts) {
                        // 1. Remove Link
                        pstmDel.setInt(1, repairId);
                        pstmDel.setInt(2, part.getItemId());
                        pstmDel.addBatch();

                        // 2. Mark Stock as AVAILABLE
                        pstmStock.setInt(1, part.getItemId());
                        pstmStock.addBatch();

                        // 3. Replace Serial Number Placeholders
                        pstmReplaceSn.setInt(1, part.getItemId());
                        pstmReplaceSn.addBatch();

                    }
                    pstmDel.executeBatch();
                    pstmStock.executeBatch();
                    pstmReplaceSn.executeBatch();
                }
            }

            connection.commit(); // COMMIT
            return true;

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    public boolean completeCheckout(int repairId, int customerId, int userId,
                                    double totalAmount, double discount,double partsTotal, double paidAmount, String paymentMethod) throws SQLException {

        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // START TRANSACTION


            // 1. DETERMINE PAYMENT STATUS
            String payStatus = "PENDING";
            if (paidAmount >= totalAmount) {
                payStatus = "PAID";
            } else if (paidAmount > 0) {
                payStatus = "PARTIAL";
            }

            String sqlRepairJob = "UPDATE RepairJob SET paid_amount = ?,total_amount = ?, discount = ? WHERE repair_id = ?";
            try(PreparedStatement pstmRepairJob = connection.prepareStatement(sqlRepairJob)) {
                pstmRepairJob.setDouble(1, paidAmount);
                pstmRepairJob.setDouble(2, totalAmount);
                pstmRepairJob.setDouble(3, discount);
                pstmRepairJob.setInt(4, repairId);
                pstmRepairJob.executeUpdate();
            }




            boolean hasParts = false;
            int  saleId = -1;
            String sqlIsPartExist = "SELECT 1 FROM RepairItem WHERE repair_id = ?";
            try(PreparedStatement pstmIsPartExist = connection.prepareStatement(sqlIsPartExist)){
                pstmIsPartExist.setInt(1, repairId);
                ResultSet rsPartExist = pstmIsPartExist.executeQuery();
                if(rsPartExist.next()){
                    hasParts = true;
                }
            }

            // 2. CREATE SALE RECORD (Sales Table)
            // Only if there are parts used
            if(hasParts){


                String sqlMarkSold = "UPDATE ProductItem SET status='SOLD', sold_date=NOW() WHERE item_id=?";
                try(PreparedStatement pstmSold = connection.prepareStatement(sqlMarkSold)) {
                    // Mark all used parts as SOLD
                    String sqlGetParts = "SELECT item_id FROM RepairItem WHERE repair_id = ?";
                    try(PreparedStatement pstmGetParts = connection.prepareStatement(sqlGetParts)){
                        pstmGetParts.setInt(1, repairId);
                        ResultSet rsParts = pstmGetParts.executeQuery();
                        while(rsParts.next()){
                            int itemId = rsParts.getInt("item_id");
                            pstmSold.setInt(1, itemId);
                            pstmSold.addBatch();
                        }
                        if(pstmSold.executeBatch().length == 0){
                            connection.rollback();
                            return false;
                        }
                    }
                }


                String sqlSale = "INSERT INTO Sales (customer_id, user_id, sale_date, sub_total, discount, grand_total, payment_status, description) " +
                        "VALUES (?, ?, NOW(), ?, 0, ?, ?, ?)";

                PreparedStatement pstmSale = connection.prepareStatement(sqlSale, java.sql.Statement.RETURN_GENERATED_KEYS);
                pstmSale.setInt(1, customerId);
                pstmSale.setInt(2, userId);
                pstmSale.setDouble(3, partsTotal); // Subtotal
                pstmSale.setDouble(4, partsTotal); // Grand Total
                pstmSale.setString(5, payStatus);
                pstmSale.setString(6, "Repair Job Checkout - Job #" + repairId);

                if (pstmSale.executeUpdate() <= 0) {
                    connection.rollback();
                    return false;
                }

                // Get the generated Sale ID
                ResultSet rs = pstmSale.getGeneratedKeys();
                if (rs.next()) {
                    saleId = rs.getInt(1);

                }else {
                    connection.rollback();
                    return false;
                }

                // 3. LINK REPAIR TO SALE (RepairSale Table)
                String sqlLink = "INSERT INTO RepairSale (repair_id, sale_id) VALUES (?, ?)";
                PreparedStatement pstmLink = connection.prepareStatement(sqlLink);
                pstmLink.setInt(1, repairId);
                pstmLink.setInt(2, saleId);
                pstmLink.executeUpdate();
            }



            // 4. RECORD THE PAYMENT (TransactionRecord Table)
            // Only if they actually paid something now

                String sqlTrans = "INSERT INTO TransactionRecord (transaction_type, payment_method, amount, flow, repair_id, user_id, reference_note) " +
                        "VALUES ('REPAIR_PAYMENT', ?, ?, 'IN', ?, ?, ?)";
                PreparedStatement pstmTrans = connection.prepareStatement(sqlTrans);
                pstmTrans.setString(1, paymentMethod);
                pstmTrans.setDouble(2, paidAmount);
                pstmTrans.setInt(3, repairId);
                pstmTrans.setInt(4, userId);
                pstmTrans.setString(5, "Repair Checkout Payment");
                pstmTrans.executeUpdate();


            // 5. UPDATE REPAIR JOB STATUS
            // Status -> DELIVERED, Payment -> Updated
            String sqlUpdateJob = "UPDATE RepairJob SET status='DELIVERED', date_out=NOW(), payment_status=? WHERE repair_id=?";
            PreparedStatement pstmJob = connection.prepareStatement(sqlUpdateJob);
            pstmJob.setString(1, payStatus);
            pstmJob.setInt(2, repairId);
            pstmJob.executeUpdate();

            connection.commit(); // COMMIT TRANSACTION
            return true;

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            e.printStackTrace();
            throw e;
        }catch (Exception ex){
            if (connection != null) connection.rollback();
            ex.printStackTrace();
            throw new SQLException("Failed to complete checkout: " + ex.getMessage());
        }finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    private ProductCondition fromConditionString(String s) {
        if (s == null) return ProductCondition.BOTH;
        try {
            if (s.equals("USED")) {
                return ProductCondition.USED;
            } else if (s.equals("BRAND NEW")) {
                return ProductCondition.BRAND_NEW;
            }
            return ProductCondition.BOTH;
        } catch (IllegalArgumentException ex) {
            return ProductCondition.BOTH; // unknown condition value
        }
    }
}