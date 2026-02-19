package lk.ijse.etecmanagementsystem.bo;

import lk.ijse.etecmanagementsystem.dao.ProductDAOImpl;
import lk.ijse.etecmanagementsystem.dao.ProductItemDAOImpl;
import lk.ijse.etecmanagementsystem.db.DBConnection;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.model.ProductModel;
import lk.ijse.etecmanagementsystem.model.UnitManagementModel;

import java.sql.Connection;
import java.sql.SQLException;

public class InventoryBOImpl {
    ProductDAOImpl productDAO = new ProductDAOImpl();
    ProductItemDAOImpl productItemDAO = new ProductItemDAOImpl();
    UnitManagementModel unitModel = new UnitManagementModel();

    public int saveProductAndGetId(ProductDTO p) throws SQLException {
        Connection connection = null;

        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Start Transaction

            boolean isSaved = productDAO.save(p);
            if (!isSaved) {
                connection.rollback();
                throw new SQLException("Failed to save product.");
            }

            int newStockId = productDAO.getLastInsertedProductId();
            if(newStockId <= 0) {
                connection.rollback();
                throw new SQLException("Failed to save product and retrieve ID.");
            }

            boolean isCreate = unitModel.createPlaceholderItems(newStockId, p.getQty());
            if (!isCreate) {
                connection.rollback();
                throw new SQLException("Failed to create placeholder items for the new product.");
            }

            connection.commit();
            return newStockId;

        } catch (Exception e) {
            if (connection != null) connection.rollback(); // Undo if error
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    public boolean update(ProductDTO p) throws SQLException {

        Connection connection = null;

        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            boolean isUpdated = productDAO.update(p);
            if (!isUpdated) {
                connection.rollback();
                return false;
            }

            boolean isItemUpdated = productItemDAO.updateCustomerWarranty(p.getWarrantyMonth(), Integer.parseInt(p.getId()));
            if (!isItemUpdated) {
                connection.rollback();
                return false;
            }

            connection.commit(); // Save both changes
            return true;

        } catch (SQLException e) {
            if (connection != null) connection.rollback(); // Undo if error
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    public boolean updateProductWithQtySync(ProductDTO p) throws SQLException {

        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Start Transaction

            boolean isUpdated = productDAO.update(p);
            if (!isUpdated) {
                connection.rollback();
                return false;
            }

            boolean isItemUpdated = productItemDAO.updateCustomerWarranty(p.getWarrantyMonth(), Integer.parseInt(p.getId()));
            if (!isItemUpdated) {
                connection.rollback();
                return false;
            }

            int currentTotalItems = productItemDAO.getAvailableItemCount(Integer.parseInt(p.getId()));
            if(currentTotalItems <= 0) {
                connection.rollback();
                throw new SQLException("Data integrity issue: No available items found for this product. Cannot sync quantity.");
            }

            int targetQty = p.getQty();
            int difference = targetQty - currentTotalItems;

            if (difference > 0) {

                boolean isCreate = productItemDAO.addPlaceHolderItem(Integer.parseInt(p.getId()), difference);
                if (!isCreate) {
                    connection.rollback();
                    throw new SQLException("Failed to create placeholder items to sync quantity.");
                }
            } else if (difference < 0) {

                boolean isDeleted = productItemDAO.deletePlaceHolderItems(Integer.parseInt(p.getId()), Math.abs(difference));
                if (!isDeleted) {
                    connection.rollback();
                    System.out.println("DEBUG: Failed to delete placeholder items to sync quantity. Difference: " + difference);
                    return false;
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    public boolean deleteById(String stockId) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            boolean isItemDeleted = new ProductItemDAOImpl().delete(Integer.parseInt(stockId));
            if (!isItemDeleted) {
                connection.rollback();
                System.out.println("DEBUG: Failed to delete product items for Stock ID " + stockId);
                return false;
            }

            ProductDAOImpl productDAO = new ProductDAOImpl();
            boolean isDeleted = productDAO.delete(Integer.parseInt(stockId));
            if (!isDeleted) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) connection.rollback();

            if (e.getMessage().contains("constraint") || e.getMessage().contains("foreign key")) {
                throw new SQLException("Cannot delete this Product because some items have already been SOLD. You cannot delete history.");
            }
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    public ProductModel.ItemDeleteStatus checkItemStatusForDelete(String stockId) throws SQLException {

        int realAvailableCount = productItemDAO.getRealItemCount(Integer.parseInt(stockId));
        System.out.println("DEBUG: Real Available Item Count for Stock ID " + stockId + " is " + realAvailableCount);
        int restrictedCount = productItemDAO.getRestrictedRealItemCount(Integer.parseInt(stockId));
        System.out.println("DEBUG: Restricted Item Count for Stock ID " + stockId + " is " + restrictedCount);

        return new ProductModel.ItemDeleteStatus(realAvailableCount, restrictedCount);
    }

    public static class ItemDeleteStatus {
        public final int realAvailableCount;
        public final int restrictedCount;

        public ItemDeleteStatus(int real, int restricted) {
            this.realAvailableCount = real;
            this.restrictedCount = restricted;
        }
    }
}
