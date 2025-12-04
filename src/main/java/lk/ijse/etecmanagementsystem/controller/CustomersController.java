package lk.ijse.etecmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.etecmanagementsystem.dto.CustomerDTO;
import lk.ijse.etecmanagementsystem.dto.SupplierDTO;
import lk.ijse.etecmanagementsystem.model.CustomersModel;
import lk.ijse.etecmanagementsystem.model.SuppliersModel;
import lk.ijse.etecmanagementsystem.service.MenuBar;
import lk.ijse.etecmanagementsystem.util.Login;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomersController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtContact;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtSearchByID;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnSearch;

    @FXML
    private TableView<CustomerDTO> tblCustomer;
    @FXML
    private TableColumn<CustomerDTO, Integer> colId;
    @FXML
    private TableColumn<CustomerDTO, String> colName;
    @FXML
    private TableColumn<CustomerDTO, String> colContact;
    @FXML
    private TableColumn<CustomerDTO, String> colEmail;
    @FXML
    private TableColumn<CustomerDTO, String> colAddress;

    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnReset;

    private final CustomersModel customerModel = new CustomersModel();
    private final ObservableList<CustomerDTO> customerObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setCellValueFactories();
        loadProducts();
        tblCustomer.setItems(customerObservableList);

        // Listener to handle selection changes
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void setCellValueFactories() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("number"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {

            CustomerDTO customer = new CustomerDTO(
                    0,
                    txtName.getText().trim(),
                    txtContact.getText().trim(),
                    txtEmail.getText().trim(),
                    txtAddress.getText().trim()
            );

            boolean isSaved = customerModel.saveCustomer(customer);
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Saved Successfully!").show();
                handleReset();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Save Customer.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "DB Error: " + e.getMessage()).show();
        }
    }

    @FXML
    private void handleUpdate() {
        if (txtId.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please select a customer to update.").show();
            return;
        }
        if (!validateFields()) return;

        try {
            int id = Integer.parseInt(txtId.getText().trim());
            CustomerDTO customer = new CustomerDTO(
                    id,
                    txtName.getText(),
                    txtContact.getText(),
                    txtEmail.getText(),
                    txtAddress.getText()
            );

            boolean isUpdated = customerModel.updateCustomer(customer);
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Updated Successfully!").show();
                handleReset();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Update Customer.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "DB Error: " + e.getMessage()).show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid ID format.").show();
        }
    }

    @FXML
    private void handleDelete() {
        if (txtId.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please select a customer to delete.").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this customer?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                boolean isDeleted = customerModel.deleteCustomer(id);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Customer Deleted Successfully!").show();
                    handleReset();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to Delete Customer.").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "DB Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    private void handleEnterNav(KeyEvent event) {

        final String ID_REGEX = "^\\d+$";

        if (event.getCode() == KeyCode.ENTER) {
            String idText = txtSearchByID.getText().trim();

            if (idText.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please enter customer ID").show();
                return;
            }
            if (!idText.matches(ID_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid ID: Must be a number (Integer) only.").show();
                return;
            }

            try {
                int cID = Integer.parseInt(idText);

                CustomerDTO c = customerModel.getCustomerById(cID);

                if (c != null) {
                    populateFields(c);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Customer ID does not exist").show();
                }

            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid ID. Please enter a number.").show();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Something went wrong! " + e.getMessage()).show();
            }
        }
    }

    @FXML
    private void handleReset() {
        txtId.setText("");
        txtName.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtSearchByID.setText("");
        txtSearch.setText("");
        reloadTable();
    }

    @FXML
    private void handleSearch() {
        String search = txtSearch.getText().toLowerCase();
        if (!search.isEmpty() ) {
            ObservableList<CustomerDTO> filteredList = filterData(search);
            tblCustomer.setItems(filteredList);
        } else {
            tblCustomer.setItems(customerObservableList);
        }
    }


    @FXML
    private void handleTableClick() {
        CustomerDTO selectedItem = tblCustomer.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            populateFields(selectedItem);
        }
    }


    private boolean validateFields() {

        final String ID_REGEX = "^\\d+$";
        final String NAME_REGEX = "^[a-zA-Z0-9\\s.\\-&]+$";
        final String CONTACT_REGEX = "^0\\d{9}$";
        final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$";
        final String ADDRESS_REGEX = "^[A-Za-z0-9, ./-]{4,}$";


        String idText = txtId.getText();
        if (!(idText == null || idText.isEmpty() ) && !idText.matches(ID_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid ID: Must be a number (Integer) only.").show();
            txtId.requestFocus();
            return false;
        }


        String nameText = txtName.getText();
        if (nameText == null || nameText.isEmpty() || !nameText.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name: Must contain at least 3 letters.").show();
            txtName.requestFocus();
            return false;
        }

        // 3. Contact Validation (THIS WAS CAUSING THE ERROR)
        String contactText = txtContact.getText();
        // Added 'contactText == null' check
        if (contactText == null || contactText.isEmpty() || !contactText.matches(CONTACT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact: Must start with 0 and have exactly 10 digits.").show();
            txtContact.requestFocus();
            return false;
        }

        // 4. Email Validation
        String emailText = txtEmail.getText();
        if (!(emailText == null || emailText.isEmpty()) && !emailText.matches(EMAIL_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Email: Please enter a valid email address.").show();
            txtEmail.requestFocus();
            return false;
        }

        // 5. Address Validation
        String addressText = txtAddress.getText();
        if (addressText == null || addressText.isEmpty() || !addressText.matches(ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Address: Must be at least 4 characters long.").show();
            txtAddress.requestFocus();
            return false;
        }

        return true;
    }

    private void reloadTable() {
        loadProducts();
        tblCustomer.setItems(customerObservableList);
    }

    private void loadProducts() {
        try {
            List<CustomerDTO> rawData = customerModel.getAllCustomers();
            customerObservableList.clear(); // Always clear before adding
            if (rawData != null) {
                customerObservableList.addAll(rawData);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading Suppliers: " + e.getMessage()).show();
        }
    }

    private ObservableList<CustomerDTO> filterData(String search) {
        String searchLower = search.toLowerCase();

        return customerObservableList.stream()
                .filter(c -> {
                    boolean matchesName = c.getName() != null && c.getName().toLowerCase().contains(searchLower);
                    boolean matchesContact = c.getNumber() != null && c.getNumber().contains(search);
                    boolean matchesEmail = c.getEmailAddress() != null && c.getEmailAddress().toLowerCase().contains(searchLower);

                    return matchesName || matchesContact || matchesEmail;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void populateFields(CustomerDTO c) {
        txtId.setText(String.valueOf(c.getId()));
        txtName.setText(c.getName());
        txtContact.setText(c.getNumber());
        txtEmail.setText(c.getEmailAddress());
        txtAddress.setText(c.getAddress());
    }
}