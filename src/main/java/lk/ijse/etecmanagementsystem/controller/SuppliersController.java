package lk.ijse.etecmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lk.ijse.etecmanagementsystem.util.Login;
import lk.ijse.etecmanagementsystem.service.MenuBar;

import java.util.ArrayList;
import java.util.List;

public class SuppliersController {

    // Inject all buttons from FXML
    @FXML private Button btnDashboard;
    @FXML private Button btnInventory;
    @FXML private Button btnRepairs;
    @FXML private Button btnSuppliers;
    @FXML private Button btnCustomers;
    @FXML private Button btnTransactions;
    @FXML private Button btnWarranty;
    @FXML private Button btnSettings;
    @FXML private Button btnUser;




    MenuBar menuBar = new MenuBar();

    @FXML
    public void initialize() {



    }
    @FXML
    private void handleSave(){

    }
    @FXML
    private void handleUpdate(){

    }
    @FXML
    private void handleDelete(){

    }
    @FXML
    private void handleClear(){}
    @FXML
    private void handleSearch(){}
    @FXML
    private void handleTableClick(){}
}