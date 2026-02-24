//package lk.ijse.etecmanagementsystem.model;
//
//
//import lk.ijse.etecmanagementsystem.dao.*;
//import lk.ijse.etecmanagementsystem.dao.entity.TransactionRecord;
//import lk.ijse.etecmanagementsystem.db.DBConnection;
//import lk.ijse.etecmanagementsystem.dto.ProductItemDTO;
//import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
//import lk.ijse.etecmanagementsystem.dto.SalesDTO;
//import lk.ijse.etecmanagementsystem.dto.tm.RepairJobTM;
//import lk.ijse.etecmanagementsystem.dto.tm.RepairPartTM;
//import lk.ijse.etecmanagementsystem.util.PaymentStatus;
//import lk.ijse.etecmanagementsystem.util.ProductCondition;
//import lk.ijse.etecmanagementsystem.util.RepairStatus;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RepairJobModel {
//
////    public List<RepairJobTM> getAllRepairJobs() throws SQLException {
////        List<RepairJobTM> list = new ArrayList<>();
////
////        Connection connection = DBConnection.getInstance().getConnection();
////
////        String sql = "SELECT r.repair_id, r.cus_id, r.user_id, r.device_name, r.device_sn, " +
////                "r.problem_desc, r.diagnosis_desc, r.repair_results, " + // <--- Added here
////                "r.status, r.date_in, r.date_out, r.labor_cost, r.parts_cost, r.total_amount, r.discount, r.payment_status, r.paid_amount, " +
////                "c.name AS cus_name, c.number AS cus_contact, " +
////                "c.email AS cus_email, c.address AS cus_address " +
////                "FROM RepairJob r " +
////                "JOIN Customer c ON r.cus_id = c.cus_id " +
////                "ORDER BY r.date_in DESC";
////
////        PreparedStatement pstm = connection.prepareStatement(sql);
////        ResultSet resultSet = pstm.executeQuery();
////
////        while (resultSet.next()) {
////            RepairJobDTO dto = new RepairJobDTO(
////                    resultSet.getInt("repair_id"),
////                    resultSet.getInt("cus_id"),
////                    resultSet.getInt("user_id"),
////                    resultSet.getString("device_name"),
////                    resultSet.getString("device_sn"),
////                    resultSet.getString("problem_desc"),
////                    resultSet.getString("diagnosis_desc"),
////                    resultSet.getString("repair_results"),
////                    RepairStatus.valueOf(resultSet.getString("status")),
////                    resultSet.getTimestamp("date_in"),
////                    resultSet.getTimestamp("date_out"),
////                    resultSet.getDouble("labor_cost"),
////                    resultSet.getDouble("parts_cost"),
////                    resultSet.getDouble("total_amount"),
////                    resultSet.getDouble("paid_amount"),
////                    resultSet.getDouble("discount"),
////                    PaymentStatus.valueOf(resultSet.getString("payment_status"))
////            );
////
////            String cusName = resultSet.getString("cus_name");
////            String cusContact = resultSet.getString("cus_contact");
////            String email = resultSet.getString("cus_email");
////            String address = resultSet.getString("cus_address");
////
////            RepairJobTM tm = new RepairJobTM(dto, cusName, cusContact, email, address);
////            list.add(tm);
////        }
////
////        return list;
////    }
//
//
////    public boolean saveRepairJob(RepairJobDTO dto) throws SQLException {
////        String sql = "INSERT INTO RepairJob " +
////                "(cus_id, user_id, device_name, device_sn, problem_desc, status, date_in, payment_status) " +
////                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
////
////        Connection connection = DBConnection.getInstance().getConnection();
////        PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
////
////        pstm.setInt(1, dto.getCusId());
////        pstm.setInt(2, dto.getUserId());
////        pstm.setString(3, dto.getDeviceName());
////        pstm.setString(4, dto.getDeviceSn());
////        pstm.setString(5, dto.getProblemDesc());
////        pstm.setString(6, dto.getStatus().name());
////
////        if (dto.getDateIn() != null) {
////            pstm.setTimestamp(7, new java.sql.Timestamp(dto.getDateIn().getTime()));
////        } else {
////            pstm.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
////        }
////
////
////        pstm.setString(8, "PENDING");
////
////        if (pstm.executeUpdate() <= 0) {
////            return false;
////        }
////
////        int repairId = -1;
////        ResultSet rs = pstm.getGeneratedKeys();
////        if (rs.next()) {
////            repairId = rs.getInt(1);
////        }
////        if (repairId > 0) {
////            dto.setRepairId(repairId);
////            return true;
////        }
////        return false;
////    }
//
////    public boolean updateRepairJob(RepairJobDTO dto) throws SQLException {
////        String sql = "UPDATE RepairJob SET cus_id=?, device_name=?, device_sn=?, problem_desc=? WHERE repair_id=?";
////
////        Connection connection = DBConnection.getInstance().getConnection();
////        PreparedStatement pstm = connection.prepareStatement(sql);
////
////        pstm.setInt(1, dto.getCusId());
////        pstm.setString(2, dto.getDeviceName());
////        pstm.setString(3, dto.getDeviceSn());
////        pstm.setString(4, dto.getProblemDesc());
////        pstm.setInt(5, dto.getRepairId());
////
////        return pstm.executeUpdate() > 0;
////    }
////
////    public boolean deleteRepairJob(int repairId) throws SQLException {
////        String sql = "DELETE FROM RepairJob WHERE repair_id=?";
////
////        Connection connection = DBConnection.getInstance().getConnection();
////        PreparedStatement pstm = connection.prepareStatement(sql);
////
////        pstm.setInt(1, repairId);
////
////        return pstm.executeUpdate() > 0;
////    }
//
//
////    public boolean updateRepairDescriptions(int repairId, String diagnosis, String results) throws SQLException {
////        String sql = "UPDATE RepairJob SET diagnosis_desc = ?, repair_results = ? WHERE repair_id = ?";
////        Connection connection = DBConnection.getInstance().getConnection();
////        PreparedStatement pstm = connection.prepareStatement(sql);
////        pstm.setString(1, diagnosis);
////        pstm.setString(2, results);
////        pstm.setInt(3, repairId);
////        return pstm.executeUpdate() > 0;
////    }
//
////    public boolean updateStatus(int repairId, RepairStatus newStatus) throws SQLException {
////        Connection connection = DBConnection.getInstance().getConnection();
////        String sql = "UPDATE RepairJob SET status = ? WHERE repair_id = ?";
////        PreparedStatement pstm = connection.prepareStatement(sql);
////        pstm.setString(1, newStatus.name());
////        pstm.setInt(2, repairId);
////        return pstm.executeUpdate() > 0;
////    }
//
//
////    public List<RepairPartTM> getUsedParts(int repairId) throws SQLException {
////        List<RepairPartTM> list = new ArrayList<>();
////
////        String sql = "SELECT pi.item_id, p.name, pi.serial_number, p.p_condition, ri.unit_price " +
////                "FROM RepairItem ri " +
////                "JOIN ProductItem pi ON ri.item_id = pi.item_id " +
////                "JOIN Product p ON pi.stock_id = p.stock_id " +
////                "WHERE ri.repair_id = ?";
////
////        Connection connection = DBConnection.getInstance().getConnection();
////        PreparedStatement pstm = connection.prepareStatement(sql);
////        pstm.setInt(1, repairId);
////        ResultSet rs = pstm.executeQuery();
////
////        while (rs.next()) {
////
////
////            list.add(new RepairPartTM(
////                    rs.getInt("item_id"),
////                    rs.getString("name"),
////                    rs.getString("serial_number"),
////                    fromConditionString(rs.getString("p_condition")),
////                    rs.getDouble("unit_price")
////            ));
////        }
////        return list;
////    }
//
////    public boolean updateRepairJobDetails(int repairId, String intake, String diagnosis, String resolution,
////                                          double laborCost, double partsCost, double totalAmount,
////                                          List<RepairPartTM> activeParts,
////                                          List<RepairPartTM> returnedParts) throws SQLException {
////
////        RepairJobDAOImpl repairJobDAO = new RepairJobDAOImpl();
////        RepairItemDAOImpl repairItemDAO = new RepairItemDAOImpl();
////        ProductItemDAOImpl productItemDAO = new ProductItemDAOImpl();
////        ProductDAOImpl productDAO = new ProductDAOImpl();
////
////        Connection connection = null;
////        try {
////            connection = DBConnection.getInstance().getConnection();
////            connection.setAutoCommit(false); // START TRANSACTION
////
////            boolean jobUpdated = repairJobDAO.updateRepairCosts(new RepairJobDTO(
////                    repairId,intake, diagnosis, resolution,
////                    laborCost, partsCost, totalAmount
////            ));
////            if (!jobUpdated) {
////                connection.rollback();
////                return false;
////            }
////
////            int repairItemId;
////            for (RepairPartTM part : activeParts) {
////                repairItemId = repairItemDAO.getRepairItemId(repairId, part.getItemId());
////                if (repairItemId == -1) {
////                    boolean linkSaved = repairItemDAO.saveRepairItem(repairId, part.getItemId(), part.getUnitPrice());
////                    if (!linkSaved) {
////                        connection.rollback();
////                        return false;
////                    }
////                    boolean marked = productItemDAO.updateItemForRepair(part.getItemId());
////                    if (!marked) {
////                        connection.rollback();
////                        return false;
////                    }
////                    boolean snFixed = productItemDAO.fixSerialForRepair(part.getItemId());
////                    if (!snFixed) {
////                        connection.rollback();
////                        return false;
////                    }
////                    int stockId = productItemDAO.getProductItem(part.getItemId()).getStockId();
////                    if (stockId <= 0) {
////                        connection.rollback();
////                        return false;
////                    }
////                    boolean qtyDecreased = productDAO.updateQty(stockId, -1);
////                    if (!qtyDecreased) {
////                        connection.rollback();
////                        return false;
////                    }
////                }
////            }
////
////
////            if (!returnedParts.isEmpty()) {
////                    for (RepairPartTM part : returnedParts) {
////
////                        boolean linkDeleted = repairItemDAO.deleteRepairItem(repairId, part.getItemId());
////                        if (!linkDeleted) {
////                            connection.rollback();
////                            return false;
////                        }
////
////                        boolean restocked = productItemDAO.updateItemAvailability(part.getItemId());
////                        if (!restocked) {
////                            connection.rollback();
////                            return false;
////                        }
////
////                        boolean snReplaced = productItemDAO.replaceSerialForReturned(part.getItemId());
////                        if (!snReplaced) {
////                            connection.rollback();
////                            return false;
////                        }
////
////                        int stockId = productItemDAO.getProductItem(part.getItemId()).getStockId();
////                        if (stockId <= 0) {
////                            connection.rollback();
////                            return false;
////                        }
////                        boolean qtyIncrease = productDAO.updateQty(stockId, 1);
////                        if (!qtyIncrease) {
////                            connection.rollback();
////                            return false;
////                        }
////
////                    }
////            }
////
////            connection.commit(); // COMMIT
////            return true;
////
////        } catch (SQLException e) {
////            if (connection != null) connection.rollback();
////            e.printStackTrace();
////            return false;
////        } finally {
////            if (connection != null) connection.setAutoCommit(true);
////        }
////    }
//
////    public boolean completeCheckout(int repairId, int customerId, int userId,
////                                    double totalAmount, double discount, double partsTotal, double paidAmount, String paymentMethod, String serialNumber) throws SQLException {
////
////        RepairItemDAOImpl repairItemDAO = new RepairItemDAOImpl();
////        RepairJobDAOImpl repairJobDAO = new RepairJobDAOImpl();
////        ProductItemDAOImpl productItemDAO = new ProductItemDAOImpl();
////        QueryDAOImpl queryDAO = new QueryDAOImpl();
////        SalesDAOImpl salesDAO = new SalesDAOImpl();
////        TransactionRecordDAOImpl transactionRecordDAO = new TransactionRecordDAOImpl();
////
////        Connection connection = null;
////        try {
////            connection = DBConnection.getInstance().getConnection();
////            connection.setAutoCommit(false); // START TRANSACTION
////
////            String payStatus = "PENDING";
////            if (paidAmount >= totalAmount) {
////                payStatus = "PAID";
////            } else if (paidAmount > 0) {
////                payStatus = "PARTIAL";
////            }
////
////            boolean isUpdated = repairJobDAO.updateRepairPayment(paidAmount, totalAmount, discount, payStatus, repairId);
////            if (!isUpdated) {
////                connection.rollback();
////                return false;
////            }
////
////            int saleId = -1;
////
////            boolean hasParts = repairItemDAO.getRepairItemByRepairId(repairId) != null;
////
////            if (hasParts) {
////                List<RepairPartTM> partsToMark = queryDAO.getUsedParts(repairId);
////                for (RepairPartTM repairPart : partsToMark) {
////                    String sn = productItemDAO.getProductItem(repairPart.getItemId()).getSerialNumber();
////                    boolean marked = productItemDAO.updateStatus(sn, "SOLD");
////                    if (!marked) {
////                        connection.rollback();
////                        return false;
////                    }
////                }
////
////                SalesDTO salesDTO = new SalesDTO();
////                salesDTO.setCustomerId(customerId);
////                salesDTO.setUserId(userId);
////                salesDTO.setSubtotal(partsTotal);
////                salesDTO.setDiscount(0);
////                salesDTO.setGrandTotal(partsTotal);
////                salesDTO.setPaidAmount(partsTotal);
////                salesDTO.setPaymentStatus(payStatus.equals("PAID") ? PaymentStatus.PAID : payStatus.equals("PARTIAL") ? PaymentStatus.PARTIAL : PaymentStatus.PENDING);
////                salesDTO.setDescription("Repair Job Checkout - Job #" + repairId);
////
////                boolean saleSaved = salesDAO.saveSale(salesDTO);
////                if (!saleSaved) {
////                    connection.rollback();
////                    return false;
////                }
////
////                int generatedSaleId = salesDAO.getLastInsertedSalesId();
////                if (generatedSaleId > 0) {
////                    saleId = generatedSaleId;
////                } else {
////                    connection.rollback();
////                    return false;
////                }
////
////                boolean linkSaved = new RepairSalesDAOImpl().saveRepairSale(repairId, saleId);
////                if (!linkSaved) {
////                    connection.rollback();
////                    return false;
////                }
////            }
////
////            boolean transactionRecorded = transactionRecordDAO.insertTransactionRecord(new TransactionRecord(
////                    "REPAIR_PAYMENT",
////                    paymentMethod,
////                    paidAmount,
////                    "IN",
////                    saleId,
////                    userId,
////                    customerId,
////                    "Repair Checkout Payment - Job #" + repairId
////            ));
////            if (!transactionRecorded) {
////                connection.rollback();
////                return false;
////            }
////
////            boolean jobUpdated = repairJobDAO.updateDateOut(payStatus, repairId);
////            if (!jobUpdated) {
////                connection.rollback();
////                return false;
////            }
////
////            connection.commit(); // COMMIT TRANSACTION
////            return true;
////
////        } catch (SQLException e) {
////            if (connection != null) connection.rollback();
////            e.printStackTrace();
////            throw e;
////        } catch (Exception ex) {
////            if (connection != null) connection.rollback();
////            ex.printStackTrace();
////            throw new SQLException("Failed to complete checkout: " + ex.getMessage());
////        } finally {
////            if (connection != null) connection.setAutoCommit(true);
////        }
////    }
//
////    private ProductCondition fromConditionString(String s) {
////        if (s == null) return ProductCondition.BOTH;
////        try {
////            if (s.equals("USED")) {
////                return ProductCondition.USED;
////            } else if (s.equals("BRAND NEW")) {
////                return ProductCondition.BRAND_NEW;
////            }
////            return ProductCondition.BOTH;
////        } catch (IllegalArgumentException ex) {
////            return ProductCondition.BOTH; // unknown condition value
////        }
////    }
//}