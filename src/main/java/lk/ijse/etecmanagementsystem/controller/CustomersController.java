package lk.ijse.etecmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lk.ijse.etecmanagementsystem.service.MenuBar;
import lk.ijse.etecmanagementsystem.util.Login;

import java.util.ArrayList;
import java.util.List;

public class CustomersController {


    // We need a list to easily loop through them
    private List<Button> menuButtons = new ArrayList<>();

    private MenuBar menuBar = new MenuBar();

    @FXML
    public void initialize() {



    }
}