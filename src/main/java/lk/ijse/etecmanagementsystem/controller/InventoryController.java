package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.App;
import lk.ijse.etecmanagementsystem.component.ProductCard;
import lk.ijse.etecmanagementsystem.component.SkeletonCard;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.model.InventoryModel;
import lk.ijse.etecmanagementsystem.service.InventoryService;
import lk.ijse.etecmanagementsystem.service.ThreadService;
import lk.ijse.etecmanagementsystem.service.MenuBar; // Assuming you have this
import lk.ijse.etecmanagementsystem.util.Category;
import lk.ijse.etecmanagementsystem.util.ProductCondition;
import lk.ijse.etecmanagementsystem.util.ProductUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class InventoryController {

    @FXML
    private TilePane productGrid;
    @FXML
    private TableView<ProductDTO> productTable;
    @FXML
    private TableColumn<ProductDTO, String> colId, colName, colCategory;
    @FXML
    private TableColumn<ProductDTO, Double> colSellPrice;
    @FXML
    private TableColumn<ProductDTO, Integer> colWarrantyMonth, colQty;

    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbCategory;
    @FXML
    private Button btnLoadMore, gridViewButton, tableViewButton;

    @FXML
    private ComboBox<ProductCondition> cmbCondition;



    private final InventoryService inventoryService = new InventoryService();

    private List<ProductDTO> allFetchedData = new ArrayList<>(); // Stores ALL results from DB
    private ObservableList<ProductDTO> tableDataList = FXCollections.observableArrayList();


    private Task<List<ProductDTO>> currentLoadTask;
    private int currentGridLimit = 10;
    private final int BATCH_SIZE = 10;
    private final int moreButtonThreshold = 48;
    private boolean isGridView = true;


    private final InventoryModel inventoryModel = new InventoryModel();


static {
    ProductUtil.productCache.clear();
}

    @FXML
    public void initialize() {


        if(ProductUtil.productCache.isEmpty()){
            setAllRawData();
        }


        setupTableColumns();

        setupControls();

        setupListeners();

        switchToGridView();
    }


    private void setAllRawData(){
        try {

            List<ProductDTO> rawData = inventoryModel.findAll();
            if(rawData != null){
                ProductUtil.productCache.setAll(rawData);
            }else {
                ProductUtil.productCache.clear();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No products found in the database.");
                alert.show();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading products: " + e.getMessage());
            alert.show();
        }
    }


    @FXML
    private void addProduct() {

        System.out.println("Add Product button clicked.");

        try {

            App.setupSecondaryStageScene("product", "Product Management");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void categoryManagement() {

        System.out.println("Category Management button clicked.");
        setCategoryStage();
    }

    @FXML
    private void handleLoadMore() {
        // Increase limit
        currentGridLimit = moreButtonThreshold;
        btnLoadMore.setVisible(false);
        renderGrid();
    }

    @FXML
    private void switchToGridView() {
        isGridView = true;

        productGrid.setVisible(true);
        productGrid.setManaged(true);
        productTable.setVisible(false);
        productTable.setManaged(false);

        gridViewButton.setDisable(true);
        tableViewButton.setDisable(false);

        refreshData(); // First load

    }

    @FXML
    private void switchToTableView() {
        isGridView = false;

        productGrid.setVisible(false);
        productGrid.setManaged(false);
        productTable.setVisible(true);
        productTable.setManaged(true);

        tableViewButton.setDisable(true);
        gridViewButton.setDisable(false);

        // Render existing data
        renderTable();

    }

    private void refreshData() {

        if (currentLoadTask != null && currentLoadTask.isRunning()) {
            currentLoadTask.cancel();
        }

        currentGridLimit = BATCH_SIZE;

        if (isGridView) {
            productGrid.getChildren().clear();
            for (int i = 0; i < 10; i++) {
                productGrid.getChildren().add(new SkeletonCard());
            }
            btnLoadMore.setVisible(false);
        }


        currentLoadTask = new Task<>() {
            @Override
            protected List<ProductDTO> call() throws Exception {
                // Simulate network/DB delay (Remove this in production)
                Thread.sleep(600);

                // Fetch ALL matching data from Service
                return inventoryService.getFilteredProducts(txtSearch.getText(), cmbCategory.getValue(), cmbCondition.getValue());
            }
        };


        currentLoadTask.setOnSucceeded(event -> {
            allFetchedData = currentLoadTask.getValue(); // Store master list

            if (isGridView) {
                renderGrid(); // Render cards
            } else {
                renderTable(); // Render table rows
            }
        });

        currentLoadTask.setOnFailed(event -> {
            Throwable e = currentLoadTask.getException();
            System.out.println("   eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee Error loading inventory: " + e.getMessage());
            e.printStackTrace(); // Log for developer

            if (isGridView) productGrid.getChildren().clear();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data Load Error");
            alert.setHeaderText("Could not load inventory.");
            alert.setContentText("Please check your database connection.\nDetails: " + e.getMessage());
            alert.showAndWait();
        });


        ThreadService.setInventoryLoadingThread(new Thread(currentLoadTask));
        ThreadService.getInventoryLoadingThread().start();
    }

    private void renderGrid() {
        productGrid.getChildren().clear(); // Clear skeletons

        if (allFetchedData.isEmpty()) {
            Label placeholder = new Label("No products found.");
            placeholder.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");
            productGrid.getChildren().add(placeholder);
            btnLoadMore.setVisible(false);
            return;
        }

        int limit = Math.min(currentGridLimit, allFetchedData.size());

        for (int i = 0; i < limit; i++) {

            ProductDTO p = allFetchedData.get(i);
            productGrid.getChildren().add(new ProductCard(p));
        }

        if (limit < allFetchedData.size()) {
            btnLoadMore.setVisible(true);
        } else {
            btnLoadMore.setVisible(false);
        }

        if (productGrid.getChildren().size() >= moreButtonThreshold) {
            btnLoadMore.setVisible(false);
        }
    }

    private void renderTable() {
        tableDataList.setAll(allFetchedData);
        productTable.setItems(tableDataList);
        btnLoadMore.setVisible(false);
    }

    private void setupControls() {
        // 1. Setup Controls
        cmbCategory.setItems(Category.getCategories());
        cmbCategory.getSelectionModel().select(null);
        cmbCondition.getItems().setAll(ProductCondition.values());
        cmbCondition.getSelectionModel().select(ProductCondition.BOTH);
        btnLoadMore.setVisible(false);
    }
    private void setCategoryStage() {
        try {
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Category");

            newStage.setScene(new Scene(App.loadFXML("category"), 400, 200));
            newStage.showAndWait();
            // After closing, refresh categories
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        // 2. Setup Listeners (Debouncing could be added here for optimization)
        txtSearch.textProperty().addListener((obs, old, newVal) -> refreshData());
        cmbCategory.valueProperty().addListener((obs, old, newVal) -> refreshData());
        cmbCondition.valueProperty().addListener((obs, old, newVal) -> refreshData());
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colWarrantyMonth.setCellValueFactory(new PropertyValueFactory<>("warrantyMonth"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
    }

}



