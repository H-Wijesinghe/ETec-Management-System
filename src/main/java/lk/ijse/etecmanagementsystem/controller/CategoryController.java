package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jdk.jfr.Event;
import lk.ijse.etecmanagementsystem.model.CategoryModel;
import lk.ijse.etecmanagementsystem.util.Category;

import java.util.List;

public class CategoryController {


    @FXML
    TextField categoryName;
    @FXML
    Button btnSave;
    @FXML
    Button btnDelete;
    @FXML
    Button btnUpdate;
    @FXML
    Button btnReset;
    @FXML
    Label lblMsg;

    CategoryModel categoryModel = new CategoryModel();

    private final String CATEGORY_REGEX = "^(?=.{3,30}$)[A-Z][a-z]*(\\\\s[A-Z][a-z]*)*";

    @FXML
    public void initialize() {
        System.out.println("CategoryController initialized");
        loadCategories();

    }

    private void loadCategories() {


        try{
            List<String> list = categoryModel.getAllCategories();
            if(!list.isEmpty()){
                Category.getCategories().clear();// Clear existing categories
                Category.getCategories().setAll(list);

                System.out.println("Categories loaded from DB: " + list);
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No categories found in the database.");
                alert.showAndWait();
            }
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load categories: " + e.getMessage());
            alert.showAndWait();
        }


    }

    @FXML
    private void addCategory() {
        System.out.println("Add category method called");
        String name = categoryName.getText().trim();
        if (name.isEmpty()) {
            lblMsg.setText("Category name cannot be empty.");
            lblMsg.setStyle("-fx-text-fill: red;");
        } else {
            ObservableList<String> categories = Category.getCategories();
            if (!name.matches(CATEGORY_REGEX)) {
                lblMsg.setText("Invalid category name. It should start with an uppercase letter and be 3-30 characters long.");
                lblMsg.setStyle("-fx-text-fill: red;");


            } else if (categories.contains(name) && !checkNomatch(name)) {
                lblMsg.setText("Category Already exists.");
                lblMsg.setStyle("-fx-text-fill: red;");

            } else {

                categories.add(name);
                lblMsg.setText("Category " + name + " added successfully.");
                lblMsg.setStyle("-fx-text-fill: green;");

                categoryName.clear();

            }

        }

    }

    @FXML
    private void deleteCategory() {
        System.out.println("Delete category method called");
        String name = categoryName.getText().trim();
        if (name.isEmpty()) {
            lblMsg.setText("Category name cannot be empty.");
            lblMsg.setStyle("-fx-text-fill: red;");
        } else {
            ObservableList<String> categories = Category.getCategories();
            if (!name.matches(CATEGORY_REGEX)) {
                lblMsg.setText("Invalid category name. It should start with an uppercase letter and be 3-30 characters long.");
                lblMsg.setStyle("-fx-text-fill: red;");


            } else if (!categories.contains(name) || checkNomatch(name)) {
                lblMsg.setText("Category not exists.");
                lblMsg.setStyle("-fx-text-fill: red;");

            } else {

                categories.remove(name);
                lblMsg.setText("Category " + name + " Deleted successfully.");
                lblMsg.setStyle("-fx-text-fill: green;");

                categoryName.clear();
            }
        }


    }

    @FXML
    private void updateCategory() {
        System.out.println("Update category method called");
        String name = categoryName.getText().trim();
        if (name.isEmpty()) {
            lblMsg.setText("Category name cannot be empty.");
            lblMsg.setStyle("-fx-text-fill: red;");
        } else {
            ObservableList<String> categories = Category.getCategories();
            if (!name.matches(CATEGORY_REGEX)) {
                lblMsg.setText("Invalid category name. It should start with an uppercase letter and be 3-30 characters long.");
                lblMsg.setStyle("-fx-text-fill: red;");


            } else if (!categories.contains(name) || checkNomatch(name)) {
                lblMsg.setText("Category not exists.");
                lblMsg.setStyle("-fx-text-fill: red;");

            } else {

                int index = categories.indexOf(name);
                if (index != -1) {
                    categories.set(index, name);
                    lblMsg.setText("Category " + name + " updated successfully.");
                    lblMsg.setStyle("-fx-text-fill: green;");


                    categoryName.clear();

                } else {
                    lblMsg.setText("Category not found for update.");
                    lblMsg.setStyle("-fx-text-fill: red;");
                }
            }

        }
    }

    private boolean checkNomatch(String name) {

        return Category.getCategories().stream()
                .noneMatch(category -> category.equalsIgnoreCase(name));// Check for case-insensitive match
        // If no match found, return true (no similar category exists)

    }

    @FXML
    private void resetCategory() {
        System.out.println("Reset category method called");
        categoryName.clear();
        lblMsg.setText("");
    }

    @FXML
    private void getEnterkey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String name = categoryName.getText().trim();
            if(categoryName.getText().isEmpty()){
                lblMsg.setText("Category name cannot be empty.");
                lblMsg.setStyle("-fx-text-fill: red;");
            }else {
                ObservableList<String> categories = Category.getCategories();
                if (!name.matches(CATEGORY_REGEX)) {
                    lblMsg.setText("Invalid category name. It should start with an uppercase letter and be 3-30 characters long.");
                    lblMsg.setStyle("-fx-text-fill: red;");
                }else if (categories.contains(name) || !checkNomatch(name)) {
                    int index = categories.indexOf(name);
                    if (index != -1) {
                        categories.set(index, name);
                        lblMsg.setText("Category " + name + " Found! You can delete or update now.");
                        lblMsg.setStyle("-fx-text-fill: green;");
                    }

                }else {
                    lblMsg.setText("Category not exists.");
                    lblMsg.setStyle("-fx-text-fill: red;");

                }

            }

        }
    }
}
