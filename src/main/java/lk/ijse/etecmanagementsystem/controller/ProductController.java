package lk.ijse.etecmanagementsystem.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.model.CategoryModel;
import lk.ijse.etecmanagementsystem.util.Category;
import lk.ijse.etecmanagementsystem.App;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;
import lk.ijse.etecmanagementsystem.util.ProductCondition;
import lk.ijse.etecmanagementsystem.util.ProductUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController implements Initializable {


    @FXML private TextField txtId;
    @FXML private TextArea txtName;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private ComboBox<ProductCondition> cmbCondition;
    @FXML private TextField txtPrice;
    @FXML private TextField txtWarranty;
    @FXML private TextField txtQty;
    @FXML private TextArea txtDescription;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnNewCategory;
    @FXML private Button btnOpenImagePopup;

    @FXML private TableView<ProductDTO> tableProducts;
    @FXML private TableColumn<ProductDTO, String> colId;
    @FXML private TableColumn<ProductDTO, String> colName;
    @FXML private TableColumn<ProductDTO, String> colCategory;
    @FXML private TableColumn<ProductDTO, String> colCondition;
    @FXML private TableColumn<ProductDTO, Double> colPrice;
    @FXML private TableColumn<ProductDTO, Integer> colWarranty;
    @FXML private TableColumn<ProductDTO, Integer> colQty;
    @FXML private TableColumn<ProductDTO, String> colDesc;


    private final ObservableList<ProductDTO> productList = FXCollections.observableArrayList();
    private String selectedImagePath = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tableProducts.setItems(ProductUtil.productCache);

        setCellValueFactories();

        initComboBoxes();

        // 4. Add Listener for Table Selection
        tableProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFields(newValue);
            }
        });

        // 5. Setup Button Actions
        setupButtonActions();
    }

    private void setCellValueFactories() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colWarranty.setCellValueFactory(new PropertyValueFactory<>("warrantyMonth"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void initComboBoxes() {

        cmbCondition.getItems().setAll(ProductCondition.values());
        cmbCategory.setItems(Category.getCategories());

    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> saveProduct());
        btnUpdate.setOnAction(event -> updateProduct());
        btnDelete.setOnAction(event -> deleteProduct());
        btnClear.setOnAction(event -> clearForm());

        // Logic for the "+" button next to Category
        btnNewCategory.setOnAction(event -> {
            setCategoryStage();
            loadCategories();
            cmbCategory.setItems(Category.getCategories());

        });

        // Logic for Image Popup
        btnOpenImagePopup.setOnAction(event -> openImagePopup());
    }
    private void loadCategories() {

        CategoryModel categoryModel = new CategoryModel();
        Category.getCategories().clear();
        try {
            List<String> list = categoryModel.getAllCategories();
            if (!list.isEmpty()) {
                Category.getCategories().setAll(list);

                System.out.println("Categories loaded from DB: " + list);
            } else {
                System.out.println("No categories found in the database.");
            }
        } catch (Exception e) {
            System.out.println("Failed to load categories: " + e.getMessage());
        }

    }

    // --- CRUD Operations ---

    private void saveProduct() {
        if (!validateFields()) return;

        ProductDTO newProduct = new ProductDTO(
                txtId.getText(),
                txtName.getText(),
                txtDescription.getText(),
                Double.parseDouble(txtPrice.getText()),
                cmbCategory.getValue(),
                cmbCondition.getValue(),
                Integer.parseInt(txtWarranty.getText()),
                Integer.parseInt(txtQty.getText()),
                selectedImagePath
        );

        // TODO: Insert into Database here (JDBC)
        ProductUtil.productCache.add(newProduct);
        tableProducts.setItems(ProductUtil.productCache);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Product Saved Successfully!");
        clearForm();
    }

    private void updateProduct() {
        ProductDTO selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a product to update.");
            return;
        }

        if (!validateFields()) return;

        // Update the object
        selected.setId(txtId.getText());
        selected.setName(txtName.getText());
        selected.setCategory(cmbCategory.getValue());
        selected.setCondition(cmbCondition.getValue());
        selected.setSellPrice(Double.parseDouble(txtPrice.getText()));
        selected.setWarrantyMonth(Integer.parseInt(txtWarranty.getText()));
        selected.setQty(Integer.parseInt(txtQty.getText()));
        selected.setDescription(txtDescription.getText());
        selected.setImagePath(selectedImagePath);

        // TODO: Update Database here (JDBC) -> "UPDATE products SET..."

        tableProducts.refresh(); // Refresh UI
        showAlert(Alert.AlertType.INFORMATION, "Success", "Product Updated Successfully!");
        clearForm();
    }

    private void deleteProduct() {
        ProductDTO selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a product to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            // TODO: Delete from Database here (JDBC) -> "DELETE FROM products..."
            ProductUtil.productCache.remove(selected);
            clearForm();
        }
    }

    private void clearForm() {
        txtId.clear();
        txtName.clear();
        cmbCategory.getSelectionModel().clearSelection();
        cmbCondition.getSelectionModel().clearSelection();
        txtPrice.clear();
        txtWarranty.clear();
        txtQty.clear();
        txtDescription.clear();
        selectedImagePath = "";
        tableProducts.getSelectionModel().clearSelection();
    }

    // --- Helper Methods ---

    private void populateFields(ProductDTO p) {
        txtId.setText(p.getId());
        txtName.setText(p.getName());
        cmbCategory.setValue(p.getCategory());
        cmbCondition.setValue(p.getCondition());
        txtPrice.setText(String.valueOf(p.getSellPrice()));
        txtWarranty.setText(String.valueOf(p.getWarrantyMonth()));
        txtQty.setText(String.valueOf(p.getQty()));
        txtDescription.setText(p.getDescription());
        selectedImagePath = p.getImagePath();
    }

    private boolean validateFields() {
        if (txtId.getText().isEmpty() || txtName.getText().isEmpty() || txtPrice.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "ID, Name, and Price are required!");
            return false;
        }
        try {
            Double.parseDouble(txtPrice.getText());
            Integer.parseInt(txtQty.getText());
            Integer.parseInt(txtWarranty.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Price, Qty, and Warranty must be numbers.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    private void openImagePopup() {
        try {


            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Select Image");
            stage.setScene(new Scene(App.loadFXML("imagePopup"),500,400));
            stage.showAndWait();

            // Logic to retrieve the selected file would go here if using a shared model/controller logic

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open image popup.");
        }
    }
    private void setCategoryStage() {
        try {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("Category");

            newStage.setScene(new Scene(App.loadFXML("category"), 400, 200));
            newStage.setResizable(false);
            newStage.showAndWait();


        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Category window: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
