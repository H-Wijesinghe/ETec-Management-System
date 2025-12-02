package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import lk.ijse.etecmanagementsystem.model.UnitManagementModel;
import lk.ijse.etecmanagementsystem.model.tm.ProductItemTM;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnitManagementController {

    // --- TAB 1: VIEW ---
    @FXML private ComboBox<String> cmbViewProduct;
    @FXML private TableView<ProductItemTM> tblView;
    @FXML private TableColumn<?, ?> colViewSerial, colViewSupplier, colViewRemaining, colViewSupWar, colViewCustWar, colViewStatus;

    // --- TAB 2: ADD ---
    @FXML private ComboBox<String> cmbProduct, cmbSupplier;
    @FXML private TextField txtSupplierWarranty, txtCustomerWarranty, txtSerialNumber;
    @FXML private Label lblStagingCount;
    @FXML private TableView<String> tblStaging;
    @FXML private TableColumn<String, String> colStagedSerial;
    @FXML private Button btnSaveAll;

    @FXML private TableView<ProductItemTM> tblHistory;
    @FXML private TableColumn<?, ?> colHistSerial, colHistSupplier, colHistSupWar, colHistCustWar, colHistStatus;

    // --- TAB 3: CORRECTION ---
    @FXML private TextField txtFixSearch, txtFixSerial, txtFixSupWar;
    @FXML private ComboBox<String> cmbFixProduct, cmbFixSupplier;
    @FXML private VBox vboxFixDetails;

    // --- TAB 4: STATUS (Updated Fields) ---
    @FXML private TextField txtStatusSearch;
    @FXML private VBox vboxStatusUpdate;
    @FXML private Label lblCurrentStatus;
    @FXML private Label lblUpdateProductName; // <--- NEW
    @FXML private Label lblUpdateSupplier;    // <--- NEW
    @FXML private ComboBox<String> cmbNewStatus;

    // --- DATA ---
    private int selectedStockId = -1;
    private String currentFixingSerial = "";
    private String currentStatusSerial = "";
    private Map<String, Integer> supplierMap;

    // MODEL INSTANCE
    private final UnitManagementModel model = new UnitManagementModel();

    private final ObservableList<String> stagingList = FXCollections.observableArrayList();
    private final ObservableList<ProductItemTM> historyList = FXCollections.observableArrayList();
    private final ObservableList<ProductItemTM> viewList = FXCollections.observableArrayList();

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
            List<String> products = model.getAllProductNames();
            ObservableList<String> prodObList = FXCollections.observableArrayList(products);
            cmbProduct.setItems(prodObList);
            cmbViewProduct.setItems(prodObList);
            cmbFixProduct.setItems(prodObList); // Tab 3

            supplierMap = model.getAllSuppliers();
            ObservableList<String> supObList = FXCollections.observableArrayList(supplierMap.keySet());
            cmbSupplier.setItems(supObList);
            cmbFixSupplier.setItems(supObList); // Tab 3

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void setupListeners() {
        cmbProduct.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if (newVal != null) handleProductSelection(newVal);
        });
        cmbViewProduct.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if (newVal != null) handleViewFilter(null);
        });
    }

    // --- TAB 1 LOGIC ---
    @FXML void handleViewFilter(ActionEvent e) {
        String p = cmbViewProduct.getValue();
        if (p == null) return;
        try {
            UnitManagementModel.ProductMeta meta = model.getProductMeta(p);
            if (meta != null) {
                viewList.clear();
                viewList.addAll(model.getUnitsByStockId(meta.getStockId()));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
    @FXML void handleRefreshView(ActionEvent e) { handleViewFilter(null); }

    // --- TAB 2 LOGIC ---
    @FXML void handleClearSupplier(ActionEvent e) { cmbSupplier.getSelectionModel().clearSelection(); }

    private void handleProductSelection(String name) {
        try {
            UnitManagementModel.ProductMeta meta = model.getProductMeta(name);
            if (meta != null) {
                selectedStockId = meta.getStockId();
                txtCustomerWarranty.setText(String.valueOf(meta.getDefaultWarranty()));
                historyList.clear();
                historyList.addAll(model.getUnitsByStockId(selectedStockId));
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

    @FXML void handleSaveAll(ActionEvent e) {
        if (selectedStockId == -1) return;
        try {
            Integer supId = (cmbSupplier.getValue() != null) ? supplierMap.get(cmbSupplier.getValue()) : null;
            int supWar = txtSupplierWarranty.getText().isEmpty() ? 0 : Integer.parseInt(txtSupplierWarranty.getText());
            int custWar = Integer.parseInt(txtCustomerWarranty.getText());
            List<String> list = new ArrayList<>(stagingList);

            if (model.saveBatch(selectedStockId, supId, supWar, custWar, list)) {
                showAlert(Alert.AlertType.INFORMATION, "Success");
                stagingList.clear();
                lblStagingCount.setText("0 Items");
                btnSaveAll.setDisable(true);
                historyList.clear();
                historyList.addAll(model.getUnitsByStockId(selectedStockId));
                if (cmbViewProduct.getValue() != null && cmbViewProduct.getValue().equals(cmbProduct.getValue())) handleViewFilter(null);
            }
        } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, ex.getMessage()); }
    }

    // --- TAB 3: CORRECTION ---
    @FXML void handleFixSearch(ActionEvent e) {
        String s = txtFixSearch.getText().trim();
        if (s.isEmpty()) return;
        try {
            ProductItemTM item = model.getItemBySerial(s);
            if (item != null) {
                currentFixingSerial = item.getSerialNumber();
                vboxFixDetails.setDisable(false);
                txtFixSerial.setText(item.getSerialNumber());
                cmbFixProduct.setValue(item.getProductName()); // Auto-Select Product
                cmbFixSupplier.setValue(item.getSupplierName()); // Auto-Select Supplier
                txtFixSupWar.setText(String.valueOf(item.getSupplierWarranty()));
            } else { showAlert(Alert.AlertType.WARNING, "Not Found"); vboxFixDetails.setDisable(true); }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    @FXML void handleFixSave(ActionEvent e) {
        try {
            String newSerial = txtFixSerial.getText().trim();
            String pName = cmbFixProduct.getValue();
            if (newSerial.isEmpty() || pName == null) return;

            int newStockId = model.getProductMeta(pName).getStockId();
            Integer newSupId = (cmbFixSupplier.getValue() != null) ? supplierMap.get(cmbFixSupplier.getValue()) : null;
            int newSupWar = Integer.parseInt(txtFixSupWar.getText());

            if (model.correctItemMistake(currentFixingSerial, newSerial, newStockId, newSupId, newSupWar)) {
                showAlert(Alert.AlertType.INFORMATION, "Corrected");
                vboxFixDetails.setDisable(true);
                txtFixSearch.clear();
                handleViewFilter(null);
            }
        } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, ex.getMessage()); }
    }

    // --- TAB 4: STATUS (With Product Name Display) ---
    @FXML void handleStatusSearch(ActionEvent e) {
        String s = txtStatusSearch.getText().trim();
        if (s.isEmpty()) return;
        try {
            ProductItemTM item = model.getItemBySerial(s);
            if (item != null) {
                currentStatusSerial = item.getSerialNumber();
                vboxStatusUpdate.setDisable(false);

                // Show Details
                lblCurrentStatus.setText(item.getStatus());
                lblUpdateProductName.setText(item.getProductName()); // <--- FIXED
                lblUpdateSupplier.setText(item.getSupplierName());   // <--- FIXED

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