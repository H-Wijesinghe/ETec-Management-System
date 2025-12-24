package lk.ijse.etecmanagementsystem.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class FieldsValidation {

    public static boolean validateCustomerFields(TextField txtName, TextField txtContact,TextField txtEmail, TextField txtAddress,TextField txtId) {




        final String ID_REGEX = "^\\d+$";
        final String NAME_REGEX = "^[a-zA-Z0-9\\s.\\-&]+$";
        final String CONTACT_REGEX = "^0\\d{9}$";
        final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$";
        final String ADDRESS_REGEX = "^[A-Za-z0-9, ./-]{4,}$";


        String idText = (txtId.getText() == null? "" : txtId.getText().trim());
        String nameText = (txtName.getText() == null? "" : txtName.getText().trim());
        String contactText = (txtContact.getText() == null? "" : txtContact.getText().trim());
        String emailText = (txtEmail.getText() == null? "" : txtEmail.getText().trim());
        String addressText = (txtAddress.getText() == null? "" : txtAddress.getText().trim());

        // 1. ID Validation
        if (!idText.isEmpty() && !idText.matches(ID_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid ID: Must be a number (Integer) only.").show();
            txtId.requestFocus();
            return true;
        }


        // 2. Name Validation
        if (nameText.isEmpty() || !nameText.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name: Must contain at least 3 letters.").show();
            txtName.requestFocus();
            return true;
        }

        // 3. Contact Validation (THIS WAS CAUSING THE ERROR)
        if (contactText.isEmpty() || !contactText.matches(CONTACT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact: Must start with 0 and have exactly 10 digits.").show();
            txtContact.requestFocus();
            return true;
        }

        // 4. Email Validation
        if (!emailText.isEmpty() && !emailText.matches(EMAIL_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Email: Please enter a valid email address.").show();
            txtEmail.requestFocus();
            return true;
        }

        // 5. Address Validation
        if (!addressText.isEmpty() && !addressText.matches(ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Address: Must be at least 4 characters long.").show();
            txtAddress.requestFocus();
            return true;
        }

        return false;
    }

    public static void formatTxtFieldAsNumber(TextField textField, boolean allowDecimal) {
        String regex = allowDecimal ? "\\d*(\\.\\d{0,2})?" : "\\d*";
        textField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regex) ? change : null));
    }

}
