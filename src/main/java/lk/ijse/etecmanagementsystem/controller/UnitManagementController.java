package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import lk.ijse.etecmanagementsystem.model.UnitManagementModel;
import lk.ijse.etecmanagementsystem.dto.ProductItemDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitManagementController {

    // --- TAB 1: VIEW ---
    @FXML private ComboBox<String> cmbViewProduct;
    @FXML private TableView<ProductItemDTO> tblView;
    @FXML private TableColumn<?, ?> colViewSerial, colViewSupplier, colViewRemaining, colViewSupWar, colViewCustWar, colViewStatus;

    // --- TAB 2: ADD ---
    @FXML private ComboBox<String> cmbProduct, cmbSupplier;
    @FXML private Label lblProductId; // <--- NEW: Add this to FXML (fx:id="lblProductId")
    @FXML private TextField txtSupplierWarranty, txtCustomerWarranty, txtSerialNumber;
    @FXML private Label lblStagingCount;
    @FXML private TableView<String> tblStaging;
    @FXML private TableColumn<String, String> colStagedSerial;
    @FXML private Button btnSaveAll;

    @FXML private TableView<ProductItemDTO> tblHistory;
    @FXML private TableColumn<?, ?> colHistSerial, colHistSupplier, colHistSupWar, colHistCustWar, colHistStatus;

    // --- TAB 3: CORRECTION ---
    @FXML private TextField txtFixSearch, txtFixSerial, txtFixSupWar;
    @FXML private ComboBox<String> cmbFixProduct, cmbFixSupplier;
    @FXML private VBox vboxFixDetails;

    // --- TAB 4: STATUS ---
    @FXML private TextField txtStatusSearch;
    @FXML private VBox vboxStatusUpdate;
    @FXML private Label lblCurrentStatus;
    @FXML private Label lblUpdateProductName;
    @FXML private Label lblUpdateSupplier;
    @FXML private ComboBox<String> cmbNewStatus;

    // --- DATA ---
    private int selectedStockId = -1;
    private String currentFixingSerial = "";
    private String currentStatusSerial = "";

    // Maps to handle Name(ID) -> ID conversion
    // Key: "Cable (ID: 55)", Value: 55
    private Map<String, Integer> productSelectionMap = new HashMap<>();
    private Map<String, Integer> supplierSelectionMap = new HashMap<>();

    // Reverse Maps to handle ID -> Name(ID) conversion (For Fix Tab)
    private Map<Integer, String> idToProductDisplayMap = new HashMap<>();
    private Map<Integer, String> idToSupplierDisplayMap = new HashMap<>();

    private final UnitManagementModel model = new UnitManagementModel();

    private final ObservableList<String> stagingList = FXCollections.observableArrayList();
    private final ObservableList<ProductItemDTO> historyList = FXCollections.observableArrayList();
    private final ObservableList<ProductItemDTO> viewList = FXCollections.observableArrayList();

    public void initialize() {
        setupTables();
        loadInitialData();
        setupListeners();
        cmbNewStatus.setItems(FXCollections.observableArrayList("AVAILABLE", "SOLD", "RMA", "RETURNED_TO_SUPPLIER", "DAMAGED"));
    }

    private void setupTables() {
        // Tab 1
        colViewSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colViewSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colViewRemaining.setCellValueFactory(new PropertyValueFactory<>("remainingLife"));
        colViewSupWar.setCellValueFactory(new PropertyValueFactory<>("supplierWarranty"));
        colViewCustWar.setCellValueFactory(new PropertyValueFactory<>("customerWarranty"));
        colViewStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tblView.setItems(viewList);

        // Tab 2
        colStagedSerial.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()));
        tblStaging.setItems(stagingList);
        colHistSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colHistSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colHistSupWar.setCellValueFactory(new PropertyValueFactory<>("supplierWarranty"));
        colHistCustWar.setCellValueFactory(new PropertyValueFactory<>("customerWarranty"));
        colHistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tblHistory.setItems(historyList);
    }

    private void loadInitialData() {
        try {
            // --- LOAD PRODUCTS (BY ID) ---
            productSelectionMap.clear();
            idToProductDisplayMap.clear();

            Map<Integer, String> dbProducts = model.getAllProductMap();
            ObservableList<String> prodObList = FXCollections.observableArrayList();

            for (Map.Entry<Integer, String> entry : dbProducts.entrySet()) {
                int id = entry.getKey();
                String name = entry.getValue();

                // Format: Name (ID: 123)
                String displayStr = name + " (ID: " + id + ")";

                productSelectionMap.put(displayStr, id);
                idToProductDisplayMap.put(id, displayStr);
                prodObList.add(displayStr);
            }

            cmbProduct.setItems(prodObList);
            cmbViewProduct.setItems(prodObList);
            cmbFixProduct.setItems(prodObList);

            // --- LOAD SUPPLIERS (BY ID) ---
            supplierSelectionMap.clear();
            idToSupplierDisplayMap.clear();

            Map<Integer, String> dbSuppliers = model.getAllSuppliersMap();
            ObservableList<String> supObList = FXCollections.observableArrayList();

            for (Map.Entry<Integer, String> entry : dbSuppliers.entrySet()) {
                int id = entry.getKey();
                String name = entry.getValue();

                String displayStr = name + " (ID: " + id + ")";

                supplierSelectionMap.put(displayStr, id);
                idToSupplierDisplayMap.put(id, displayStr);
                supObList.add(displayStr);
            }

            cmbSupplier.setItems(supObList);
            cmbFixSupplier.setItems(supObList);

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void setupListeners() {
        // Look up ID based on Selection String
        cmbProduct.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if (newVal != null) {
                Integer id = productSelectionMap.get(newVal);
                if (id != null) handleProductSelection(id, newVal);
            }
        });

        cmbViewProduct.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if (newVal != null) handleViewFilter(null);
        });
    }

    // --- TAB 1 LOGIC ---
    @FXML void handleViewFilter(ActionEvent e) {
        String selection = cmbViewProduct.getValue();
        if (selection == null) return;

        Integer stockId = productSelectionMap.get(selection);
        if (stockId == null) return;

        try {
            // Get Name just for the DTO display (optional, can extract from selection string too)
            String name = selection.substring(0, selection.lastIndexOf(" (ID:"));

            viewList.clear();
            viewList.addAll(model.getUnitsByStockId(stockId, name));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    @FXML void handleRefreshView(ActionEvent e) { handleViewFilter(null); }

    // --- TAB 2 LOGIC ---
    @FXML void handleClearSupplier(ActionEvent e) { cmbSupplier.getSelectionModel().clearSelection(); }

    private void handleProductSelection(int stockId, String fullDisplayName) {
        try {
            // Update Label to show ID side-by-side
            if (lblProductId != null) lblProductId.setText("ID: " + stockId);

            UnitManagementModel.ProductMeta meta = model.getProductMetaById(stockId);
            if (meta != null) {
                selectedStockId = meta.getStockId();
                txtCustomerWarranty.setText(String.valueOf(meta.getDefaultWarranty()));

                historyList.clear();
                // Pass name for display
                String cleanName = fullDisplayName.substring(0, fullDisplayName.lastIndexOf(" (ID:"));
                historyList.addAll(model.getUnitsByStockId(selectedStockId, cleanName));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML void handleAddToStaging(ActionEvent e) {
        String serial = txtSerialNumber.getText().trim();
        if (serial.isEmpty()) return;
        if (stagingList.contains(serial)) { showAlert(Alert.AlertType.WARNING, "Duplicate in list"); return; }

        try {
            if (model.getItemBySerial(serial) != null) {
                showAlert(Alert.AlertType.ERROR, "Duplicate in DB"); return;
            }
        } catch (SQLException ex) { ex.printStackTrace(); return; }

        stagingList.add(serial);
        txtSerialNumber.clear();
        lblStagingCount.setText(stagingList.size() + " Items");
        btnSaveAll.setDisable(false);
        txtSerialNumber.requestFocus();
    }

    @FXML void handleRemoveFromStaging(ActionEvent e) {
        String s = tblStaging.getSelectionModel().getSelectedItem();
        if (s != null) {
            stagingList.remove(s);
            lblStagingCount.setText(stagingList.size() + " Items");
            if (stagingList.isEmpty()) btnSaveAll.setDisable(true);
        }
    }

    @FXML
    void handleSaveAll(ActionEvent e) {
        if (selectedStockId == -1) return;

        // Get Common Data
        Integer supId = (cmbSupplier.getValue() != null) ? supplierSelectionMap.get(cmbSupplier.getValue()) : 0;
        int supWar = txtSupplierWarranty.getText().isEmpty() ? 0 : Integer.parseInt(txtSupplierWarranty.getText());
        int custWar = Integer.parseInt(txtCustomerWarranty.getText());

        try {
            int successCount = 0;

            // Loop through every serial in your Staging Table
            for (String serial : stagingList) {

                ProductItemDTO itemDTO = new ProductItemDTO();
                itemDTO.setStockId(selectedStockId);
                itemDTO.setSupplierId(supId);
                itemDTO.setSerialNumber(serial);
                itemDTO.setSupplierWarranty(supWar);
                itemDTO.setCustomerWarranty(custWar);

                // Call the SMART method
                // This will auto-detect if it needs to fill a slot or add a new one
                if (model.registerRealItem(itemDTO)) {
                    successCount++;
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Successfully registered " + successCount + " items.");

            // Cleanup UI
            stagingList.clear();
            lblStagingCount.setText("0 Items");
            btnSaveAll.setDisable(true);

            // Refresh History Table
            String currentComboVal = cmbProduct.getValue();
            if(currentComboVal != null) {
                historyList.clear();
                String cleanName = currentComboVal.substring(0, currentComboVal.lastIndexOf(" (ID:"));
                historyList.addAll(model.getUnitsByStockId(selectedStockId, cleanName));
            }

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "One of these Serial Numbers already exists!");
            } else {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        }
    }

//    @FXML void handleSaveAll(ActionEvent e) {
//        if (selectedStockId == -1) return;
//        try {
//            // Get ID from Map
//            Integer supId = (cmbSupplier.getValue() != null) ? supplierSelectionMap.get(cmbSupplier.getValue()) : null;
//
//            int supWar = txtSupplierWarranty.getText().isEmpty() ? 0 : Integer.parseInt(txtSupplierWarranty.getText());
//            int custWar = Integer.parseInt(txtCustomerWarranty.getText());
//            List<String> list = new ArrayList<>(stagingList);
//
//            if (model.saveBatch(selectedStockId, supId, supWar, custWar, list)) {
//                showAlert(Alert.AlertType.INFORMATION, "Success");
//                stagingList.clear();
//                lblStagingCount.setText("0 Items");
//                btnSaveAll.setDisable(true);
//
//                // Refresh History
//                String currentComboVal = cmbProduct.getValue();
//                if(currentComboVal != null) {
//                    historyList.clear();
//                    String cleanName = currentComboVal.substring(0, currentComboVal.lastIndexOf(" (ID:"));
//                    historyList.addAll(model.getUnitsByStockId(selectedStockId, cleanName));
//                }
//
//                // Refresh View if match
//                if (cmbViewProduct.getValue() != null && cmbViewProduct.getValue().equals(currentComboVal)) handleViewFilter(null);
//            }
//        } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, ex.getMessage()); }
//    }

    // --- TAB 3: CORRECTION ---
    @FXML void handleFixSearch(ActionEvent e) {
        String s = txtFixSearch.getText().trim();
        if (s.isEmpty()) return;
        try {
            ProductItemDTO item = model.getItemBySerial(s);
            if (item != null) {
                currentFixingSerial = item.getSerialNumber();
                vboxFixDetails.setDisable(false);
                txtFixSerial.setText(item.getSerialNumber());
                txtFixSupWar.setText(String.valueOf(item.getSupplierWarranty()));

                // --- CRITICAL FIX: Select correct ID in ComboBox ---
                UnitManagementModel.ItemIds ids = model.getIdsBySerial(s);
                if (ids != null) {
                    // Find the string "Cable (ID: 5)" using the ID
                    String prodStr = idToProductDisplayMap.get(ids.stockId);
                    if (prodStr != null) cmbFixProduct.setValue(prodStr);

                    if (ids.supplierId != 0) {
                        String supStr = idToSupplierDisplayMap.get(ids.supplierId);
                        if (supStr != null) cmbFixSupplier.setValue(supStr);
                    } else {
                        cmbFixSupplier.setValue(null);
                    }
                }
            } else { showAlert(Alert.AlertType.WARNING, "Not Found"); vboxFixDetails.setDisable(true); }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    @FXML void handleFixSave(ActionEvent e) {
        try {
            String newSerial = txtFixSerial.getText().trim();
            String pVal = cmbFixProduct.getValue();
            if (newSerial.isEmpty() || pVal == null) return;

            // Get IDs
            int newStockId = productSelectionMap.get(pVal);
            Integer newSupId = (cmbFixSupplier.getValue() != null) ? supplierSelectionMap.get(cmbFixSupplier.getValue()) : null;
            int newSupWar = Integer.parseInt(txtFixSupWar.getText());

            if (model.correctItemMistake(currentFixingSerial, newSerial, newStockId, newSupId, newSupWar)) {
                showAlert(Alert.AlertType.INFORMATION, "Corrected");
                vboxFixDetails.setDisable(true);
                txtFixSearch.clear();
                handleViewFilter(null);
            }
        } catch (Exception ex) {
            if(ex.getMessage().contains("Duplicate entry")) {
                new Alert(Alert.AlertType.WARNING, "The new serial number already exists. Please use a different serial number.").showAndWait();
                return;
            }
            showAlert(Alert.AlertType.ERROR, ex.getMessage()); }
    }

    // --- TAB 4: STATUS ---
    @FXML void handleStatusSearch(ActionEvent e) {
        String s = txtStatusSearch.getText().trim();
        if (s.isEmpty()) return;
        try {
            ProductItemDTO item = model.getItemBySerial(s);
            if (item != null) {
                currentStatusSerial = item.getSerialNumber();
                vboxStatusUpdate.setDisable(false);

                lblCurrentStatus.setText(item.getStatus());
                lblUpdateProductName.setText(item.getProductName());
                lblUpdateSupplier.setText(item.getSupplierName());

                cmbNewStatus.setValue(item.getStatus());
            } else { showAlert(Alert.AlertType.WARNING, "Not Found"); vboxStatusUpdate.setDisable(true); }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    @FXML void handleStatusSave(ActionEvent e) {
        String st = cmbNewStatus.getValue();
        if (st == null) return;
        try {
            if (model.updateItemStatus(currentStatusSerial, st)) {
                showAlert(Alert.AlertType.INFORMATION, "Updated");
                vboxStatusUpdate.setDisable(true);
                txtStatusSearch.clear();
                handleViewFilter(null);
            }
        } catch (SQLException ex) { showAlert(Alert.AlertType.ERROR, ex.getMessage()); }
    }

    private void showAlert(Alert.AlertType t, String m) {
        new Alert(t, m).show();
    }
}