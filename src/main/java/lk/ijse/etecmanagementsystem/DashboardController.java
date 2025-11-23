package lk.ijse.etecmanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lk.ijse.etecmanagementsystem.service.Login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    // Inject all buttons from FXML
    @FXML private Button btnDashboard;
    @FXML private Button btnInventory;
    @FXML private Button btnRepairs;
    @FXML private Button btnSuppliers;
    @FXML private Button btnCustomers;
    @FXML private Button btnPayments;
    @FXML private Button btnWarranty;
    @FXML private Button btnSettings;
    @FXML private Button btnUser;

    // List to hold them for easy iteration
    private List<Button> menuButtons = new ArrayList<>();
    @FXML private Label lblUserName; // Make sure you added fx:id="lblUserName" to the user label in FXML

    @FXML
    public void initialize() {
        // Add all buttons to the list
        menuButtons.add(btnDashboard);
        menuButtons.add(btnInventory);
        menuButtons.add(btnRepairs);
        menuButtons.add(btnSuppliers);
        menuButtons.add(btnCustomers);
        menuButtons.add(btnPayments);
        menuButtons.add(btnWarranty);
        menuButtons.add(btnSettings);
        menuButtons.add(btnUser);


        String username = Login.getUserName();
        btnUser.setText(username);
    }

    // This ONE method handles clicks for ALL buttons
    @FXML
    private void handleNavClicks(ActionEvent event) {
        // 1. Identify which button was clicked
        Button clickedButton = (Button) event.getSource();

        // 2. Loop through all buttons to reset them
        for (Button btn : menuButtons) {
            // Remove the "active" class from everyone
            btn.getStyleClass().remove("active");
        }

        // 3. Add "active" class to the clicked button
        clickedButton.getStyleClass().add("active");

        // 4. (Optional) Load different content based on button
        String key = clickedButton.getId() != null ? clickedButton.getId() : clickedButton.getText();
            switch (key) {
                case "btnDashboard":
                    System.out.println("Load Dashboard View");

                    break;
                case "btnInventory":
                    System.out.println("Load Inventory View");
                    try {
                        App.setRoot("inventory");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "btnRepairs":
                    System.out.println("Load Repairs View");
                    break;
                case "btnSuppliers":
                    System.out.println("Load Suppliers View");
                    break;
                case "btnCustomers":
                    System.out.println("Load Customers View");
                    break;
                case "btnPayments":
                    System.out.println("Load Payments View");
                    break;
                case "btnWarranty":
                    System.out.println("Load Warranty View");
                    break;
                case "btnSettings":
                    System.out.println("Load Settings View");
                    break;
                case "btnUser":
                    System.out.println("Load User View");
                    break;
                default:
                    System.out.println("Unknown navigation target: " + key);
            }
    }
}