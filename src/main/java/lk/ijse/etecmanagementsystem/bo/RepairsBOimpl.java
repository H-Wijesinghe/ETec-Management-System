package lk.ijse.etecmanagementsystem.bo;

import lk.ijse.etecmanagementsystem.dao.custom.impl.*;
import lk.ijse.etecmanagementsystem.dao.entity.TransactionRecord;
import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.dto.SalesDTO;
import lk.ijse.etecmanagementsystem.dto.tm.RepairPartTM;
import lk.ijse.etecmanagementsystem.util.PaymentStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RepairsBOimpl {
    RepairJobDAOImpl repairJobDAO = new RepairJobDAOImpl();

    public List<RepairJobDTO> getAllRepairJobs() throws SQLException {

        List<RepairJobDTO> repairJobs = repairJobDAO.getAllRepairJobs();
        return repairJobs;

    }

    public boolean updateRepairJobDetails(int repairId, String intake, String diagnosis, String resolution,
                                          double laborCost, double partsCost, double totalAmount,
                                          List<RepairPartTM> activeParts,
                                          List<RepairPartTM> returnedParts) throws SQLException {

        RepairJobDAOImpl repairJobDAO = new RepairJobDAOImpl();
        RepairItemDAOImpl repairItemDAO = new RepairItemDAOImpl();
        ProductItemDAOImpl productItemDAO = new ProductItemDAOImpl();
        ProductDAOImpl productDAO = new ProductDAOImpl();

        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // START TRANSACTION

            boolean jobUpdated = repairJobDAO.updateRepairCosts(new RepairJobDTO(
                    repairId, intake, diagnosis, resolution,
                    laborCost, partsCost, totalAmount
            ));
            if (!jobUpdated) {
                connection.rollback();
                System.out.println("Failed to update repair job details 1");
                return false;
            }

            int repairItemId;
            for (RepairPartTM part : activeParts) {
                repairItemId = repairItemDAO.getRepairItemId(repairId, part.getItemId());

                if (repairItemId == -1) {
                    boolean linkSaved = repairItemDAO.saveRepairItem(repairId, part.getItemId(), part.getUnitPrice());
                    if (!linkSaved) {
                        connection.rollback();
                        System.out.println("Failed to update repair job details 2");
                        return false;
                    }
                    boolean marked = productItemDAO.updateItemForRepair(part.getItemId());
                    if (!marked) {
                        connection.rollback();
                        System.out.println("Failed to update repair job details 3");
                        return false;
                    }

                    boolean snFixed = productItemDAO.fixSerialForRepair(part.getItemId());
//                    if (!snFixed) {
//                        connection.rollback();
//                        System.out.println("Failed to update repair job details 4");
//
//                        return false;
//                    }

                    int stockId = productItemDAO.getProductItem(part.getItemId()).getStockId();
                    if (stockId <= 0) {
                        connection.rollback();
                        System.out.println("Failed to update repair job details 5");

                        return false;
                    }
                    boolean qtyDecreased = productDAO.updateQty(stockId, -1);
                    if (!qtyDecreased) {
                        connection.rollback();
                        System.out.println("Failed to update repair job details 6");

                        return false;
                    }


                }
            }

            for (RepairPartTM part : returnedParts) {

                boolean linkDeleted = repairItemDAO.deleteRepairItem(repairId, part.getItemId());
                if (!linkDeleted) {
                    connection.rollback();
                    System.out.println("Failed to update repair job details 7");

                    return false;
                }


                boolean snReplaced = productItemDAO.replaceSerialForReturned(part.getItemId());
//                if (!snReplaced) {
//                    connection.rollback();
//                    System.out.println("Failed to update repair job details 8");
//
//                    return false;
//                }

                boolean restocked = productItemDAO.updateItemAvailability(part.getItemId());
                if (!restocked) {
                    connection.rollback();
                    System.out.println("Failed to update repair job details 8");

                    return false;
                }

                int stockId = productItemDAO.getProductItem(part.getItemId()).getStockId();
                if (stockId <= 0) {
                    connection.rollback();
                    System.out.println("Failed to update repair job details 10");

                    return false;
                }

                boolean qtyIncrease = productDAO.updateQty(stockId, 1);
                if (!qtyIncrease) {
                    connection.rollback();
                    System.out.println("Failed to update repair job details 11");

                    return false;
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
                                    double totalAmount, double discount, double partsTotal, double paidAmount, String paymentMethod, String serialNumber) throws SQLException {


        RepairItemDAOImpl repairItemDAO = new RepairItemDAOImpl();
        RepairJobDAOImpl repairJobDAO = new RepairJobDAOImpl();
        ProductItemDAOImpl productItemDAO = new ProductItemDAOImpl();
        QueryDAOImpl queryDAO = new QueryDAOImpl();
        SalesDAOImpl salesDAO = new SalesDAOImpl();
        TransactionRecordDAOImpl transactionRecordDAO = new TransactionRecordDAOImpl();

        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // START TRANSACTION

            String payStatus = "PENDING";
            if (paidAmount >= totalAmount) {
                payStatus = "PAID";
            } else if (paidAmount > 0) {
                payStatus = "PARTIAL";
            }

            boolean isUpdated = repairJobDAO.updateRepairPayment(paidAmount, totalAmount, discount, payStatus, repairId);
            if (!isUpdated) {
                connection.rollback();
                System.out.println("Failed to update repair payment");
                return false;
            }

            int saleId = -1;

            boolean hasParts = repairItemDAO.getRepairItemByRepairId(repairId) != null;
            System.out.println("Repair ID " + repairId + " has parts: " + hasParts);

            if (hasParts) {
                List<RepairPartTM> partsToMark = queryDAO.getUsedParts(repairId);
                for (RepairPartTM repairPart : partsToMark) {
                    String sn = productItemDAO.getProductItem(repairPart.getItemId()).getSerialNumber();
                    boolean marked = productItemDAO.updateStatus(sn, "SOLD");
                    if (!marked) {
                        connection.rollback();
                        System.out.println("Failed to mark part as sold: " + sn);
                        return false;
                    }
                }

                SalesDTO salesDTO = new SalesDTO();
                salesDTO.setCustomerId(customerId);
                salesDTO.setUserId(userId);
                salesDTO.setSubtotal(partsTotal);
                salesDTO.setDiscount(0);
                salesDTO.setGrandTotal(partsTotal);
                salesDTO.setPaidAmount(partsTotal);
                salesDTO.setPaymentStatus(payStatus.equals("PAID") ? PaymentStatus.PAID : payStatus.equals("PARTIAL") ? PaymentStatus.PARTIAL : PaymentStatus.PENDING);
                salesDTO.setDescription("Repair Job Checkout - Job #" + repairId);

                boolean saleSaved = salesDAO.saveSale(salesDTO);
                if (!saleSaved) {
                    connection.rollback();
                    return false;
                }

                int generatedSaleId = salesDAO.getLastInsertedSalesId();

                System.out.println("Generated Sale ID: " + generatedSaleId);

                if (generatedSaleId > 0) {
                    saleId = generatedSaleId;
                } else {
                    connection.rollback();
                    return false;
                }

                boolean linkSaved = new RepairSalesDAOImpl().saveRepairSale(repairId, saleId);
                if (!linkSaved) {
                    connection.rollback();
                    return false;
                }
            }

            boolean transactionRecorded = transactionRecordDAO.insertTransactionRecord(new TransactionRecord(
                    "REPAIR_PAYMENT",
                    paymentMethod,
                    paidAmount,
                    "IN",
                    repairId,
                    userId,
                    customerId,
                    "Repair Checkout Payment - Job #" + repairId
            ));
            if (!transactionRecorded) {
                connection.rollback();
                return false;
            }

            boolean jobUpdated = repairJobDAO.updateDateOut(payStatus, repairId);
            if (!jobUpdated) {
                connection.rollback();
                return false;
            }

            connection.commit(); // COMMIT TRANSACTION
            return true;

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            e.printStackTrace();
            throw e;
        } catch (Exception ex) {
            if (connection != null) connection.rollback();
            ex.printStackTrace();
            throw new SQLException("Failed to complete checkout: " + ex.getMessage());
        } finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

}
