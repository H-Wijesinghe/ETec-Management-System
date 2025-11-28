package lk.ijse.etecmanagementsystem.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import lk.ijse.etecmanagementsystem.component.ProductCard;
import lk.ijse.etecmanagementsystem.component.SkeletonCard;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.service.InventoryService;
import lk.ijse.etecmanagementsystem.service.ThreadService;
import lk.ijse.etecmanagementsystem.util.MenuBar; // Assuming you have this

import java.util.ArrayList;
import java.util.List;

public class InventoryController {

    // --- FXML UI Elements ---
    @FXML private TilePane productGrid;
    @FXML private TableView<ProductDTO> productTable;
    @FXML private TableColumn<ProductDTO, String> colId, colName, colCategory;
    @FXML private TableColumn<ProductDTO, Double> colSellPrice;
    @FXML private TableColumn<ProductDTO, Integer> colWarrantyMonth, colQty;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private Button btnLoadMore, gridViewButton, tableViewButton;
    @FXML private Label lblPageInfo;

    // Side Menu Buttons (Optional, kept from your code)
    @FXML private Button btnDashboard, btnInventory, btnRepairs, btnSuppliers, btnCustomers, btnTransactions, btnWarranty, btnSettings, btnUser;

    // --- State Management ---
    private final InventoryService inventoryService = new InventoryService();
    private final MenuBar menuBar = new MenuBar();

    // Data Containers
    private List<ProductDTO> allFetchedData = new ArrayList<>(); // Stores ALL results from DB
    private ObservableList<ProductDTO> tableDataList = FXCollections.observableArrayList();

    // Pagination / Threading
    private Task<List<ProductDTO>> currentLoadTask;
    private int currentGridLimit = 10; // Start with 10 items
    private final int BATCH_SIZE = 10; // Load 10 more on click
    private final int moreButtonThreshold = 48;
    private boolean isGridView = true; // Track current view mode

    @FXML
    public void initialize() {
        setupMenu();
        setupTableColumns();

        // 1. Setup Controls
        cmbCategory.setItems(FXCollections.observableArrayList("All Categories", "Electronics", "Accessories", "Parts"));
        cmbCategory.getSelectionModel().selectFirst();
        btnLoadMore.setVisible(false); // Hide until data loads

        // 2. Setup Listeners (Debouncing could be added here for optimization)
        txtSearch.textProperty().addListener((obs, old, newVal) -> refreshData());
        cmbCategory.valueProperty().addListener((obs, old, newVal) -> refreshData());

        // 3. Initial View State
        switchToGridView();
    }

    /**
     * MAIN DATA LOADING LOGIC
     * 1. Cancels old tasks.
     * 2. Shows skeletons (if in grid mode).
     * 3. Fetches data in background.
     * 4. Updates UI on success or shows error on failure.
     */
    private void refreshData() {
        // A. Cancel running task
        if (currentLoadTask != null && currentLoadTask.isRunning()) {
            currentLoadTask.cancel();
        }

        // B. Reset Pagination
        currentGridLimit = BATCH_SIZE;

        // C. Show Skeletons ONLY if in Grid View
        if (isGridView) {
            productGrid.getChildren().clear();
            for (int i = 0; i < 10; i++) {
                productGrid.getChildren().add(new SkeletonCard());
            }
            btnLoadMore.setVisible(false);
        }


        // D. Create Background Task
        currentLoadTask = new Task<>() {
            @Override
            protected List<ProductDTO> call() throws Exception {
                // Simulate network/DB delay (Remove this in production)
                Thread.sleep(600);

                // Fetch ALL matching data from Service
                return inventoryService.getFilteredProducts(txtSearch.getText(), cmbCategory.getValue());
            }
        };


        // E. Handle Success
        currentLoadTask.setOnSucceeded(event -> {
            allFetchedData = currentLoadTask.getValue(); // Store master list

            if (isGridView) {
                renderGrid(); // Render cards
            } else {
                renderTable(); // Render table rows
            }
        });

        // F. Handle Failure (Exception Handling)
        currentLoadTask.setOnFailed(event -> {
            Throwable e = currentLoadTask.getException();
            e.printStackTrace(); // Log for developer

            // Remove skeletons
            if (isGridView) productGrid.getChildren().clear();

            // Show User Alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data Load Error");
            alert.setHeaderText("Could not load inventory.");
            alert.setContentText("Please check your database connection.\nDetails: " + e.getMessage());
            alert.showAndWait();
        });

        // G. Start Task
        ThreadService.setInventoryLoadingThread(new Thread(currentLoadTask));

        ThreadService.getInventoryLoadingThread().start();
    }

    /**
     * Renders the Grid View based on 'currentGridLimit' (Pagination Logic)
     */
    private void renderGrid() {
        productGrid.getChildren().clear(); // Clear skeletons

        if (allFetchedData.isEmpty()) {
            Label placeholder = new Label("No products found.");
            placeholder.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");
            productGrid.getChildren().add(placeholder);
            btnLoadMore.setVisible(false);
            return;
        }

        // Slice the data: Show only from 0 to currentGridLimit
        int limit = Math.min(currentGridLimit, allFetchedData.size());

        for (int i = 0; i < limit; i++) {
            // Create Card Component
            ProductDTO p = allFetchedData.get(i);
            productGrid.getChildren().add(new ProductCard(p));
        }

        // Update "Load More" Button Logic
        if (limit < allFetchedData.size()) {
            btnLoadMore.setVisible(true);
//            lblPageInfo.setText("Showing " + limit + " of " + allFetchedData.size());
        } else {
            btnLoadMore.setVisible(false);
//            lblPageInfo.setText("All " + allFetchedData.size() + " products shown.");
        }

        if (allFetchedData.size() <= moreButtonThreshold)
            btnLoadMore.setVisible(false);
        else {
            btnLoadMore.setVisible(true);
        }
    }

    /**
     * Renders the Table View (Tables handle large data better, so we usually show all)
     */
    private void renderTable() {
        tableDataList.setAll(allFetchedData);
        productTable.setItems(tableDataList);
        // Hide "Load More" in table view as tables have scrollbars
        btnLoadMore.setVisible(false);
//        lblPageInfo.setText(allFetchedData.size() + " records found.");
    }

    // --- Event Handlers ---

    @FXML
    private void handleLoadMore() {
        // Increase limit
        currentGridLimit += moreButtonThreshold;
        // Re-render grid (It's fast enough to re-render from memory)
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

        // Render existing data without re-fetching from DB

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

    // --- Initialization Helpers ---

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colWarrantyMonth.setCellValueFactory(new PropertyValueFactory<>("warrantyMonth"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
    }

    private void setupMenu() {
        menuBar.setActive(btnInventory);

        menuBar.setupButton(btnDashboard);
        menuBar.setupButton(btnInventory);
        menuBar.setupButton(btnRepairs);
        menuBar.setupButton(btnSuppliers);
        menuBar.setupButton(btnCustomers);
        menuBar.setupButton(btnTransactions);
        menuBar.setupButton(btnWarranty);
        menuBar.setupButton(btnSettings);
        menuBar.setupButton(btnUser);
    }
}