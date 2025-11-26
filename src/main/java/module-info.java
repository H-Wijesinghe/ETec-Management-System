module lk.ijse.etecmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;



    opens lk.ijse.etecmanagementsystem to javafx.fxml;
    exports lk.ijse.etecmanagementsystem;
    exports lk.ijse.etecmanagementsystem.controller;
    opens lk.ijse.etecmanagementsystem.controller to javafx.fxml;
    exports lk.ijse.etecmanagementsystem.util;
    opens lk.ijse.etecmanagementsystem.util to javafx.fxml;

}