package lk.ijse.etecmanagementsystem.util;

import lk.ijse.etecmanagementsystem.App;
import lk.ijse.etecmanagementsystem.db.DBConnection;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class GenerateInvoice {
    public static void generateInvoice(int id, String jasper, String invoiceType) {
        try {

            String path = "reports/"+jasper+".jasper";

            InputStream reportStream = App.class.getResourceAsStream(path);

            if (reportStream == null) {
                System.err.println("Error: Could not find salesReceipt.jasper at " + path);
                return;
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            if(invoiceType.equalsIgnoreCase("SALE")){
                parameters.put("saleId", id);
            } else if (invoiceType.equalsIgnoreCase("REPAIR")) {
                parameters.put("repairId", id);
            }

//            parameters.put("saleId", saleId);

            Connection connection = DBConnection.getInstance().getConnection();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperViewer.viewReport(jasperPrint, false); // false = Don't close app on exit

        } catch (JRException | java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
